package dev.guimaker.dialog;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.dialog.type.Dialog;
import dev.guimaker.config.WorldDataPaths;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import dev.guimaker.util.PlaceholderResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Persistent managed dialogs compiled into Mojang's unmodified 1.21.6+
 * dialog types. Enhanced types are server-side templates, not custom codecs.
 */
public final class DialogRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9_./-]{1,64}");
    private static final TreeMap<String, JsonObject> DIALOGS = new TreeMap<>();
    private static MinecraftServer server;

    private DialogRepository() {}

    public enum ManagedType {
        NOTICE("minecraft:notice"), CONFIRMATION("minecraft:confirmation"),
        MULTI_ACTION("minecraft:multi_action"), SERVER_LINKS("minecraft:server_links"),
        DIALOG_LIST("minecraft:dialog_list"), FORM("guimaker:form"),
        SEARCHABLE_LIST("guimaker:searchable_list"), WIZARD("guimaker:wizard");
        private final String id;
        ManagedType(String id) { this.id = id; }
        public String id() { return id; }
        public static ManagedType parse(String raw) {
            return valueOf(raw.toUpperCase(Locale.ROOT).replace('-', '_'));
        }
    }

    public enum ManagedInputType {
        TEXT("minecraft:text"), BOOLEAN("minecraft:boolean"),
        NUMBER_RANGE("minecraft:number_range"), SINGLE_OPTION("minecraft:single_option"),
        VALIDATED_TEXT("guimaker:validated_text"),
        SEARCHABLE_DROPDOWN("guimaker:searchable_dropdown"),
        ITEM_SELECTOR("guimaker:item_selector");
        private final String id;
        ManagedInputType(String id) { this.id = id; }
        public String id() { return id; }
        public static ManagedInputType parse(String raw) {
            return valueOf(raw.toUpperCase(Locale.ROOT).replace('-', '_'));
        }
    }

    private static Path dataFile() {
        return WorldDataPaths.file("dialogs.json");
    }

    public static synchronized void load(MinecraftServer minecraftServer) {
        server = minecraftServer;
        DIALOGS.clear();
        if (!Files.exists(dataFile())) { save(); return; }
        try (Reader reader = Files.newBufferedReader(dataFile(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject entries = root.getAsJsonObject("dialogs");
            if (entries != null) {
                for (var entry : entries.entrySet()) {
                    try {
                        String id = validateId(entry.getKey());
                        JsonObject value = entry.getValue().getAsJsonObject();
                        compile(id, value);
                        DIALOGS.put(id, value.deepCopy());
                    } catch (Exception error) {
                        LOGGER.warn("Skipped invalid dialog '{}': {}", entry.getKey(), error.getMessage());
                    }
                }
            }
            LOGGER.info("GUI Maker loaded {} managed dialog(s).", DIALOGS.size());
        } catch (Exception error) {
            LOGGER.error("Could not read dialogs.json; using an empty registry.", error);
        }
    }

    public static synchronized void close() { save(); DIALOGS.clear(); server = null; }
    public static synchronized Collection<String> ids() { return List.copyOf(DIALOGS.keySet()); }

    public static synchronized void create(String rawId, ManagedType type, String title) {
        String id = validateId(rawId);
        if (DIALOGS.containsKey(id)) throw new IllegalArgumentException("Dialog already exists: " + id);
        JsonObject value = minimal(type, title);
        compile(id, value);
        DIALOGS.put(id, value); save();
    }

    public static synchronized void delete(String rawId) {
        String id = validateId(rawId);
        if (DIALOGS.remove(id) == null) throw new IllegalArgumentException("Dialog not found: " + id);
        save();
    }

    public static synchronized void setTitle(String id, String title) {
        JsonObject value = require(id); value.addProperty("title", sanitize(title, 128));
        compile(validateId(id), value); save();
    }

    public static synchronized void setBody(String id, String body) {
        JsonObject value = require(id); value.add("body", body(sanitize(body, 4096)));
        compile(validateId(id), value); save();
    }

    public static synchronized void setType(String rawId, ManagedType type) {
        String id = validateId(rawId); JsonObject old = require(id);
        JsonObject replacement = minimal(type, old.get("title").getAsString());
        if (old.has("body")) replacement.add("body", old.get("body").deepCopy());
        compile(id, replacement); DIALOGS.put(id, replacement); save();
    }

    public static synchronized void addInput(String rawId, String key,
                                             ManagedInputType type, String label) {
        if (!key.matches("[A-Za-z0-9_]{1,32}"))
            throw new IllegalArgumentException("Input key must use letters, digits or underscore.");
        String id = validateId(rawId); JsonObject value = require(id); JsonArray inputs = inputArray(value);
        for (JsonElement e : inputs) if (e.getAsJsonObject().get("key").getAsString().equals(key))
            throw new IllegalArgumentException("Input key already exists: " + key);
        JsonObject input = new JsonObject(); input.addProperty("key", key);
        input.addProperty("type", type.id()); input.addProperty("label", sanitize(label, 128));
        input.addProperty("width", 300);
        switch (type) {
            case TEXT -> { input.addProperty("initial", ""); input.addProperty("max_length", 256); }
            case BOOLEAN -> { input.addProperty("initial", false); input.addProperty("on_true", "true"); input.addProperty("on_false", "false"); }
            case NUMBER_RANGE -> { input.addProperty("start", 0); input.addProperty("end", 100); input.addProperty("initial", 0); input.addProperty("step", 1); }
            case SINGLE_OPTION, SEARCHABLE_DROPDOWN -> addDefaultOptions(input);
            case VALIDATED_TEXT -> { input.addProperty("initial", ""); input.addProperty("max_length", 256); input.addProperty("pattern", ".*"); }
            case ITEM_SELECTOR -> { input.addProperty("initial", "minecraft:stone"); input.addProperty("allow_air", false); }
        }
        inputs.add(input); compile(id, value); save();
    }

    public static synchronized void removeInput(String rawId, String key) {
        String id = validateId(rawId); JsonObject value = require(id); JsonArray inputs = inputArray(value);
        for (int i=0;i<inputs.size();i++) if (inputs.get(i).getAsJsonObject().get("key").getAsString().equals(key)) {
            inputs.remove(i); compile(id, value); save(); return;
        }
        throw new IllegalArgumentException("Input not found: " + key);
    }

    public static synchronized void addDropdownOption(String rawId, String key, String optionId, String label) {
        String id = validateId(rawId); JsonObject value = require(id);
        if (value.get("type").getAsString().equals("guimaker:searchable_list")
                && value.get("search_key").getAsString().equals(key)) {
            value.getAsJsonArray("options").add(option(optionId, label)); compile(id, value); save(); return;
        }
        for (JsonElement e : inputArray(value)) {
            JsonObject input=e.getAsJsonObject(); String type=input.get("type").getAsString();
            if (input.get("key").getAsString().equals(key)
                    && (type.endsWith("single_option") || type.endsWith("searchable_dropdown"))) {
                input.getAsJsonArray("options").add(option(optionId,label)); compile(id,value); save(); return;
            }
        }
        throw new IllegalArgumentException("Option input not found: " + key);
    }

    public static synchronized void addWizardStep(String rawId, String title) {
        String id=validateId(rawId); JsonObject value=require(id);
        if (!value.get("type").getAsString().equals("guimaker:wizard")) throw new IllegalArgumentException("Dialog is not a wizard.");
        value.getAsJsonArray("steps").add(step(title)); compile(id,value); save();
    }

    public static synchronized void removeWizardStep(String rawId, int index) {
        String id=validateId(rawId); JsonObject value=require(id);
        if (!value.get("type").getAsString().equals("guimaker:wizard")) throw new IllegalArgumentException("Dialog is not a wizard.");
        JsonArray steps=value.getAsJsonArray("steps");
        if (steps.size()<=1) throw new IllegalArgumentException("The final wizard step cannot be removed.");
        if (index<0||index>=steps.size()) throw new IllegalArgumentException("Wizard step index is out of range.");
        steps.remove(index); compile(id,value); save();
    }

    public static synchronized void addPresetAction(String rawId, String label, String presetId) {
        if (!dev.guimaker.gate.CommandPresetRegistry.isRegistered(presetId))
            throw new IllegalArgumentException("Command preset not found: " + presetId);
        String id=validateId(rawId); JsonObject value=require(id);
        Set<String> requiredKeys = dev.guimaker.util.PlaceholderResolver
                .keyPlaceholders(dev.guimaker.gate.CommandPresetRegistry.get(presetId).command());
        Set<String> availableKeys = new HashSet<>();
        for (JsonElement element : inputArray(value)) {
            availableKeys.add(element.getAsJsonObject().get("key").getAsString());
        }
        if (!availableKeys.containsAll(requiredKeys)) {
            Set<String> missing = new LinkedHashSet<>(requiredKeys);
            missing.removeAll(availableKeys);
            throw new IllegalArgumentException("Dialog is missing input key(s): " + String.join(", ", missing));
        }
        JsonObject button=presetButton(label,presetId);
        switch (value.get("type").getAsString()) {
            case "guimaker:searchable_list" -> value.add("submit",button);
            case "guimaker:wizard" -> value.getAsJsonArray("steps").get(0).getAsJsonObject().getAsJsonArray("actions").add(button);
            case "minecraft:notice" -> value.add("action",button);
            case "minecraft:confirmation" -> value.add("yes",button);
            case "minecraft:server_links", "minecraft:dialog_list" -> value.add("exit_action",button);
            default -> value.getAsJsonArray("actions").add(button);
        }
        compile(id,value); save();
    }

    public static synchronized List<String> listActions(String rawId) {
        JsonObject value = require(rawId);
        List<String> result = new ArrayList<>();
        String type = value.get("type").getAsString();
        if (type.equals("minecraft:notice")) {
            result.add("0: " + actionLabel(value.getAsJsonObject("action")));
        } else if (type.equals("minecraft:confirmation")) {
            result.add("0: " + actionLabel(value.getAsJsonObject("yes")) + " (yes)");
            result.add("1: " + actionLabel(value.getAsJsonObject("no")) + " (no)");
        } else if (type.equals("guimaker:searchable_list")) {
            result.add("0: " + actionLabel(value.getAsJsonObject("submit")) + " (submit)");
        } else if (type.equals("minecraft:server_links") || type.equals("minecraft:dialog_list")) {
            if (value.has("exit_action")) result.add("0: " + actionLabel(value.getAsJsonObject("exit_action")) + " (exit)");
        } else {
            JsonArray actions = type.equals("guimaker:wizard")
                    ? value.getAsJsonArray("steps").get(0).getAsJsonObject().getAsJsonArray("actions")
                    : value.getAsJsonArray("actions");
            for (int i = 0; i < actions.size(); i++) {
                result.add(i + ": " + actionLabel(actions.get(i).getAsJsonObject()));
            }
        }
        return result;
    }

    public static synchronized void removeAction(String rawId, int index) {
        String id = validateId(rawId);
        JsonObject value = require(id);
        String type = value.get("type").getAsString();
        if (type.equals("minecraft:notice")) {
            if (index != 0) throw new IllegalArgumentException("Action index is out of range.");
            value.add("action", button("Done", null));
        } else if (type.equals("minecraft:confirmation")) {
            if (index == 0) value.add("yes", button("Yes", null));
            else if (index == 1) value.add("no", button("No", null));
            else throw new IllegalArgumentException("Action index is out of range.");
        } else if (type.equals("guimaker:searchable_list")) {
            if (index != 0) throw new IllegalArgumentException("Action index is out of range.");
            value.add("submit", button("Select", null));
        } else if (type.equals("minecraft:server_links") || type.equals("minecraft:dialog_list")) {
            if (index != 0 || !value.has("exit_action")) throw new IllegalArgumentException("Action index is out of range.");
            value.remove("exit_action");
        } else {
            JsonArray actions = type.equals("guimaker:wizard")
                    ? value.getAsJsonArray("steps").get(0).getAsJsonObject().getAsJsonArray("actions")
                    : value.getAsJsonArray("actions");
            if (index < 0 || index >= actions.size()) throw new IllegalArgumentException("Action index is out of range.");
            actions.remove(index);
            if (actions.isEmpty() && !type.equals("guimaker:wizard")) actions.add(button("Done", null));
        }
        compile(id, value);
        save();
    }

    public static synchronized Dialog get(String rawId, ServerPlayerEntity player) {
        String id = validateId(rawId);
        JsonObject personalized = PlaceholderResolver.resolveJson(require(id), player).getAsJsonObject();
        return compile(id, personalized);
    }
    public static synchronized Dialog search(String rawId, String query, ServerPlayerEntity player) {
        String id=validateId(rawId);
        JsonObject value=PlaceholderResolver.resolveJson(require(id), player).getAsJsonObject();
        if (!value.get("type").getAsString().equals("guimaker:searchable_list")) throw new IllegalArgumentException("Dialog is not searchable.");
        String lower=query.toLowerCase(Locale.ROOT); JsonObject nativeValue=commonCopy(value,"minecraft:multi_action");
        JsonArray actions=new JsonArray(); JsonObject submit=value.getAsJsonObject("submit");
        for (JsonElement e:value.getAsJsonArray("options")) {
            JsonObject o=e.getAsJsonObject(); String oid=o.get("id").getAsString(); String label=o.get("display").getAsString();
            if (oid.toLowerCase(Locale.ROOT).contains(lower)||label.toLowerCase(Locale.ROOT).contains(lower)) {
                JsonObject b=submit.deepCopy(); b.addProperty("label",label); actions.add(b);
            }
        }
        if (actions.isEmpty()) actions.add(button("No results",null));
        nativeValue.add("actions",actions); nativeValue.addProperty("columns",1);
        return parseNative(nativeValue);
    }
    public static synchronized JsonObject json(String id) { return require(id).deepCopy(); }

    private static Dialog compile(String id, JsonObject source) {
        String type=source.get("type").getAsString(); JsonObject nativeValue;
        if (type.equals("guimaker:form")) {
            nativeValue=commonCopy(source,"minecraft:multi_action"); nativeValue.add("actions",source.get("actions").deepCopy()); nativeValue.addProperty("columns",source.get("columns").getAsInt());
        } else if (type.equals("guimaker:searchable_list")) {
            nativeValue=commonCopy(source,"minecraft:multi_action"); JsonArray inputs=new JsonArray();
            JsonObject search=new JsonObject(); search.addProperty("type","minecraft:text"); search.addProperty("key","query"); search.addProperty("label","Search"); search.addProperty("max_length",128); inputs.add(search); nativeValue.add("inputs",inputs);
            JsonObject action=new JsonObject(); action.addProperty("label","Search"); JsonObject click=new JsonObject(); click.addProperty("type","minecraft:dynamic/run_command"); click.addProperty("template","guimaker dialog search "+id+" $(query)"); action.add("action",click);
            JsonArray actions=new JsonArray(); actions.add(action); nativeValue.add("actions",actions); nativeValue.addProperty("columns",1);
        } else if (type.equals("guimaker:wizard")) {
            nativeValue=compileWizard(source,0);
        } else {
            nativeValue=source.deepCopy(); transformInputs(nativeValue);
        }
        return parseNative(nativeValue);
    }

    private static JsonObject compileWizard(JsonObject root,int index) {
        JsonObject s=root.getAsJsonArray("steps").get(index).getAsJsonObject(); JsonObject n=commonCopy(root,"minecraft:multi_action");
        n.add("title",s.get("title").deepCopy()); n.add("body",body(s.get("body").getAsString())); n.add("inputs",s.get("inputs").deepCopy()); transformInputs(n);
        JsonArray actions=s.getAsJsonArray("actions").deepCopy();
        if (index+1<root.getAsJsonArray("steps").size()) { JsonObject next=button("Next",null); JsonObject a=new JsonObject(); a.addProperty("type","minecraft:show_dialog"); a.add("dialog",compileWizard(root,index+1)); next.add("action",a); actions.add(next); }
        if (actions.isEmpty()) actions.add(button("Done",null)); n.add("actions",actions); n.addProperty("columns",1); return n;
    }

    private static void transformInputs(JsonObject dialog) {
        if (!dialog.has("inputs")) return;
        for (JsonElement e:dialog.getAsJsonArray("inputs")) { JsonObject i=e.getAsJsonObject(); String type=i.get("type").getAsString();
            if (type.equals("guimaker:validated_text")) { i.addProperty("type","minecraft:text"); i.remove("pattern"); }
            else if (type.equals("guimaker:item_selector")) { i.addProperty("type","minecraft:text"); i.remove("allow_air"); i.addProperty("max_length",256); }
            else if (type.equals("guimaker:searchable_dropdown")) { i.addProperty("type","minecraft:single_option"); String initial=i.has("initial")?i.get("initial").getAsString():""; i.remove("initial"); for(JsonElement oe:i.getAsJsonArray("options")) oe.getAsJsonObject().addProperty("initial",oe.getAsJsonObject().get("id").getAsString().equals(initial)); }
        }
    }

    private static Dialog parseNative(JsonObject value) {
        RegistryOps<JsonElement> ops=RegistryOps.of(JsonOps.INSTANCE,server.getRegistryManager());
        return Dialog.CODEC.parse(ops,value).resultOrPartial(m->LOGGER.warn("Dialog decode failed: {}",m)).orElseThrow(()->new IllegalArgumentException("Invalid native dialog definition"));
    }

    private static JsonObject minimal(ManagedType type,String title) {
        JsonObject v=common(type.id(),title); switch(type) {
            case NOTICE -> v.add("action",button("Done",null));
            case CONFIRMATION -> { v.add("yes",button("Yes",null)); v.add("no",button("No",null)); }
            case MULTI_ACTION,FORM -> { JsonArray a=new JsonArray(); a.add(button("Done",null)); v.add("actions",a); v.addProperty("columns",1); }
            case SERVER_LINKS -> { v.addProperty("columns",1); v.addProperty("button_width",200); }
            case DIALOG_LIST -> { v.add("dialogs",new JsonArray()); v.addProperty("columns",1); v.addProperty("button_width",200); }
            case SEARCHABLE_LIST -> { v.addProperty("search_key","selection"); JsonArray o=new JsonArray(); o.add(option("option","Option")); v.add("options",o); v.add("submit",button("Select",null)); }
            case WIZARD -> { JsonArray steps=new JsonArray(); steps.add(step(title)); v.add("steps",steps); }
        } return v;
    }

    private static JsonObject common(String type,String title) { JsonObject v=new JsonObject(); v.addProperty("type",type); v.addProperty("title",sanitize(title,128)); v.addProperty("can_close_with_escape",true); v.addProperty("pause",false); v.addProperty("after_action","close"); v.add("body",new JsonArray()); v.add("inputs",new JsonArray()); return v; }
    private static JsonObject commonCopy(JsonObject src,String type) { JsonObject v=common(type,src.get("title").getAsString()); for(String k:List.of("body","inputs","can_close_with_escape","pause","after_action","external_title","exit_action")) if(src.has(k)) v.add(k,src.get(k).deepCopy()); transformInputs(v); return v; }
    private static JsonArray body(String text) { JsonArray a=new JsonArray(); if(!text.isBlank()){ JsonObject m=new JsonObject(); m.addProperty("type","minecraft:plain_message"); m.addProperty("contents",text); m.addProperty("width",400); a.add(m);} return a; }
    private static JsonObject step(String title) { JsonObject s=new JsonObject(); s.addProperty("title",sanitize(title,128)); s.addProperty("body",""); s.add("inputs",new JsonArray()); s.add("actions",new JsonArray()); return s; }
    private static JsonObject option(String id,String label) { JsonObject o=new JsonObject(); o.addProperty("id",sanitize(id,64)); o.addProperty("display",sanitize(label,128)); return o; }
    private static void addDefaultOptions(JsonObject i){ JsonArray a=new JsonArray(); a.add(option("option","Option")); i.add("options",a); i.addProperty("initial","option"); }
    private static JsonObject button(String label,String preset){ JsonObject b=new JsonObject(); b.addProperty("label",sanitize(label,128)); if(preset!=null){ JsonObject a=new JsonObject(); a.addProperty("type","minecraft:run_command"); a.addProperty("command","guimaker dialog run-preset "+preset); b.add("action",a);} return b; }
    private static String actionLabel(JsonObject button) {
        if (button == null || !button.has("label")) return "<unnamed>";
        JsonElement label = button.get("label");
        if (label.isJsonPrimitive()) return label.getAsString();
        if (label.isJsonObject() && label.getAsJsonObject().has("text"))
            return label.getAsJsonObject().get("text").getAsString();
        return label.toString();
    }

    private static JsonObject presetButton(String label, String presetId) {
        String presetCommand = dev.guimaker.gate.CommandPresetRegistry.get(presetId).command();
        Set<String> keys = dev.guimaker.util.PlaceholderResolver.keyPlaceholders(presetCommand);
        JsonObject button = new JsonObject();
        button.addProperty("label", sanitize(label, 128));
        JsonObject action = new JsonObject();
        if (keys.isEmpty()) {
            action.addProperty("type", "minecraft:run_command");
            action.addProperty("command", "guimaker dialog run-preset " + presetId);
        } else {
            action.addProperty("type", "minecraft:dynamic/run_command");
            StringBuilder template = new StringBuilder("guimaker dialog run-preset-input ")
                    .append(presetId);
            for (String key : keys) {
                template.append(' ').append(key).append(' ').append("$(").append(key).append(')');
            }
            action.addProperty("template", template.toString());
        }
        button.add("action", action);
        return button;
    }
    private static JsonArray inputArray(JsonObject v){return v.get("type").getAsString().equals("guimaker:wizard")?v.getAsJsonArray("steps").get(0).getAsJsonObject().getAsJsonArray("inputs"):v.getAsJsonArray("inputs");}
    private static JsonObject require(String raw){String id=validateId(raw);JsonObject v=DIALOGS.get(id);if(v==null)throw new IllegalArgumentException("Dialog not found: "+id);return v;}
    private static String validateId(String raw){String id=raw.toLowerCase(Locale.ROOT);if(!VALID_ID.matcher(id).matches())throw new IllegalArgumentException("Dialog ID must be 1-64 characters using a-z, 0-9, _, ., / or -.");return id;}
    private static String sanitize(String v,int max){String s=v==null?"":v.replaceAll("\\p{Cntrl}","").strip();return s.length()>max?s.substring(0,max):s;}

    private static synchronized void save(){if(server==null)return;try{Files.createDirectories(dataFile().getParent());JsonObject r=new JsonObject();r.addProperty("format",2);JsonObject e=new JsonObject();DIALOGS.forEach((id,v)->e.add(id,v));r.add("dialogs",e);Path t=dataFile().resolveSibling(dataFile().getFileName()+".tmp");try(Writer w=Files.newBufferedWriter(t,StandardCharsets.UTF_8)){GSON.toJson(r,w);}try{Files.move(t,dataFile(),StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException x){Files.move(t,dataFile(),StandardCopyOption.REPLACE_EXISTING);}}catch(Exception x){LOGGER.error("Could not save dialogs.json",x);}}
}
