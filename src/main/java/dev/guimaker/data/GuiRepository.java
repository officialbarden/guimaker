package dev.guimaker.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.guimaker.action.ButtonAction;
import dev.guimaker.config.LimitsConfig;
import dev.guimaker.config.WorldDataPaths;
import dev.guimaker.dialog.DialogRepository;
import dev.guimaker.gate.ActionAllowlistGate;
import dev.guimaker.gate.CommandPresetRegistry;
import dev.guimaker.widget.WidgetDefinition;
import dev.guimaker.widget.WidgetStateRepository;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Server-side persistent GUI storage. Mutations are immediately written to
 * <world>/data/guimaker/guis.json using an atomic replace where supported.
 */
public final class GuiRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final int FORMAT_VERSION = 1;
    private static final TreeMap<String, GuiDefinition> GUIS = new TreeMap<>();

    private static MinecraftServer server;

    private GuiRepository() {}

    private static Path dataFile() {
        return WorldDataPaths.file("guis.json");
    }

    public static synchronized void load(MinecraftServer minecraftServer) {
        server = minecraftServer;
        WorldDataPaths.initialize(minecraftServer);
        LimitsConfig.load();
        CommandPresetRegistry.load();
        DialogRepository.load(minecraftServer);
        WidgetStateRepository.load(minecraftServer);
        GUIS.clear();

        if (!Files.exists(dataFile())) {
            installDefaultGui();
            save();
            LOGGER.info("GUI Maker created the default configuration: {}", dataFile());
            return;
        }

        try (Reader reader = Files.newBufferedReader(dataFile(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray guis = root.getAsJsonArray("guis");
            if (guis == null) {
                throw new IllegalArgumentException("The 'guis' array is missing");
            }
            for (JsonElement element : guis) {
                if (GUIS.size() >= LimitsConfig.maxGuis()) {
                    LOGGER.warn("GUI limit ({}) reached; remaining entries were skipped.",
                            LimitsConfig.maxGuis());
                    break;
                }
                try {
                    GuiDefinition gui = decodeGui(element.getAsJsonObject());
                    GUIS.put(gui.id(), gui);
                } catch (Exception entryError) {
                    LOGGER.warn("Skipped invalid GUI entry: {}", entryError.getMessage());
                }
            }

            if (GUIS.isEmpty()) {
                installDefaultGui();
                save();
            }
            LOGGER.info("GUI Maker loaded {} GUI(s).", GUIS.size());
        } catch (Exception error) {
            LOGGER.error("Could not read the GUI Maker configuration; the invalid file will be backed up.", error);
            backupBrokenConfig();
            GUIS.clear();
            installDefaultGui();
            save();
        }
    }

    public static synchronized void close() {
        if (server != null) {
            save();
        }
        GUIS.clear();
        DialogRepository.close();
        WidgetStateRepository.close();
        CommandPresetRegistry.clear();
        WorldDataPaths.clear();
        server = null;
    }

    public static synchronized void reload(MinecraftServer minecraftServer) {
        load(minecraftServer);
    }

    public static synchronized Collection<String> ids() {
        return Collections.unmodifiableList(GUIS.keySet().stream().toList());
    }

    public static synchronized Collection<GuiDefinition> all() {
        return Collections.unmodifiableList(GUIS.values().stream().toList());
    }

    public static synchronized GuiDefinition get(String rawId) {
        if (rawId == null) {
            return null;
        }
        return GUIS.get(rawId.toLowerCase(Locale.ROOT));
    }

    public static synchronized GuiDefinition requireGui(String rawId) {
        String id = GuiDefinition.normalizeAndValidateId(rawId);
        GuiDefinition gui = GUIS.get(id);
        if (gui == null) {
            throw new IllegalArgumentException("GUI not found: " + id);
        }
        return gui;
    }

    public static synchronized PageDefinition requirePage(String rawGuiId, int pageId) {
        GuiDefinition gui = requireGui(rawGuiId);
        PageDefinition page = gui.page(pageId);
        if (page == null) {
            throw new IllegalArgumentException("Page not found: " + pageId);
        }
        return page;
    }

    public static synchronized GuiDefinition create(String rawId, int rows, String title) {
        String id = GuiDefinition.normalizeAndValidateId(rawId);
        if (GUIS.containsKey(id)) {
            throw new IllegalArgumentException("This GUI already exists: " + id);
        }
        if (GUIS.size() >= LimitsConfig.maxGuis()) {
            throw new IllegalArgumentException("The server can contain at most " + LimitsConfig.maxGuis() + " GUIs (limits.json).");
        }
        GuiDefinition gui = new GuiDefinition(id);
        gui.addPage(new PageDefinition(0, title, rows));
        GUIS.put(id, gui);
        save();
        return gui;
    }

    public static synchronized void delete(String rawId) {
        String id = GuiDefinition.normalizeAndValidateId(rawId);
        if (!GUIS.containsKey(id)) {
            throw new IllegalArgumentException("GUI not found: " + id);
        }
        if (GUIS.size() <= 1) {
            throw new IllegalArgumentException("The last GUI cannot be removed; create another GUI first.");
        }
        GUIS.remove(id);
        save();
    }

    public static synchronized void addPage(String guiId, int pageId, int rows, String title) {
        requireGui(guiId).addPage(new PageDefinition(pageId, title, rows));
        save();
    }

    public static synchronized void renamePage(String guiId, int pageId, String title) {
        requirePage(guiId, pageId).setTitle(title);
        save();
    }

    public static synchronized void removePage(String guiId, int pageId) {
        requireGui(guiId).removePage(pageId);
        save();
    }

    public static synchronized void setItem(String guiId, int pageId, int slot, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("You must hold an item in your main hand.");
        }
        requirePage(guiId, pageId).setItem(slot, stack.copy());
        save();
    }

    public static synchronized void clearButton(String guiId, int pageId, int slot) {
        requirePage(guiId, pageId).clearButton(slot);
        save();
    }

    public static synchronized void clearAction(String guiId, int pageId, int slot) {
        requirePage(guiId, pageId).setAction(slot, ButtonAction.NO_OP, "");
        save();
    }

    public static synchronized void setAction(String guiId, int pageId, int slot,
                                              ButtonAction action, String parameter) {
        String safeParameter = parameter == null ? "" : parameter.strip();
        if (!ActionAllowlistGate.isAllowed(action, safeParameter)) {
            throw new IllegalArgumentException("The action or parameter is not allowlisted.");
        }
        if (action == ButtonAction.OPEN_PAGE) {
            int targetPage = Integer.parseInt(safeParameter);
            if (!requireGui(guiId).hasPage(targetPage)) {
                throw new IllegalArgumentException("Target page not found: " + targetPage);
            }
        }
        requirePage(guiId, pageId).setAction(slot, action, safeParameter);
        save();
    }

    public static synchronized void setWidget(String guiId, int pageId, int slot,
                                              WidgetDefinition widget) {
        requirePage(guiId, pageId).setWidget(slot, widget);
        save();
    }

    public static synchronized void clearWidget(String guiId, int pageId, int slot) {
        requirePage(guiId, pageId).clearWidget(slot);
        save();
    }

    public static synchronized void save() {
        if (server == null) {
            return;
        }
        try {
            Files.createDirectories(dataFile().getParent());
            JsonObject root = new JsonObject();
            root.addProperty("format", FORMAT_VERSION);
            JsonArray guis = new JsonArray();
            for (GuiDefinition gui : GUIS.values()) {
                guis.add(encodeGui(gui));
            }
            root.add("guis", guis);

            Path temporary = dataFile().resolveSibling(dataFile().getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            try {
                Files.move(temporary, dataFile(), StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, dataFile(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception error) {
            LOGGER.error("Could not save the GUI Maker configuration.", error);
        }
    }

    private static JsonObject encodeGui(GuiDefinition gui) {
        JsonObject object = new JsonObject();
        object.addProperty("id", gui.id());
        JsonArray pages = new JsonArray();
        for (PageDefinition page : gui.pages()) {
            JsonObject pageObject = new JsonObject();
            pageObject.addProperty("id", page.id());
            pageObject.addProperty("title", page.title());
            pageObject.addProperty("rows", page.rows());
            JsonArray buttons = new JsonArray();
            for (Map.Entry<Integer, ButtonDefinition> entry : page.buttons().entrySet()) {
                JsonObject buttonObject = new JsonObject();
                buttonObject.addProperty("slot", entry.getKey());
                ButtonDefinition button = entry.getValue();
                if (!button.displayStack().isEmpty()) {
                    buttonObject.add("stack", encodeStack(button.displayStack()));
                }
                buttonObject.addProperty("action", button.action().name());
                if (!button.parameter().isEmpty()) {
                    buttonObject.addProperty("parameter", button.parameter());
                }
                if (button.widget() != null) {
                    buttonObject.add("widget", button.widget().toJson());
                }
                buttons.add(buttonObject);
            }
            pageObject.add("buttons", buttons);
            pages.add(pageObject);
        }
        object.add("pages", pages);
        return object;
    }

    private static GuiDefinition decodeGui(JsonObject object) {
        GuiDefinition gui = new GuiDefinition(object.get("id").getAsString());
        JsonArray pages = object.getAsJsonArray("pages");
        if (pages == null || pages.isEmpty()) {
            throw new IllegalArgumentException("The GUI has no pages");
        }
        for (JsonElement pageElement : pages) {
            JsonObject pageObject = pageElement.getAsJsonObject();
            int pageId = pageObject.get("id").getAsInt();
            int rows = pageObject.get("rows").getAsInt();
            String title = pageObject.get("title").getAsString();
            PageDefinition page = new PageDefinition(pageId, title, rows);
            JsonArray buttons = pageObject.getAsJsonArray("buttons");
            if (buttons != null) {
                for (JsonElement buttonElement : buttons) {
                    JsonObject buttonObject = buttonElement.getAsJsonObject();
                    int slot = buttonObject.get("slot").getAsInt();
                    ItemStack stack = buttonObject.has("stack")
                            ? decodeStack(buttonObject.get("stack")) : ItemStack.EMPTY;
                    ButtonAction action = ButtonAction.fromSafeString(
                            buttonObject.has("action") ? buttonObject.get("action").getAsString() : "NO_OP");
                    String parameter = buttonObject.has("parameter")
                            ? buttonObject.get("parameter").getAsString() : "";
                    if (!ActionAllowlistGate.isAllowed(action, parameter)) {
                        action = ButtonAction.NO_OP;
                        parameter = "";
                    }
                    WidgetDefinition widget = buttonObject.has("widget")
                            ? WidgetDefinition.fromJson(buttonObject.getAsJsonObject("widget")) : null;
                    page.putButton(slot, new ButtonDefinition(stack, action, parameter, widget));
                }
            }
            gui.addPage(page);
        }
        return gui;
    }

    private static JsonElement encodeStack(ItemStack stack) {
        RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, server.getRegistryManager());
        return ItemStack.CODEC.encodeStart(ops, stack)
                .resultOrPartial(message -> LOGGER.warn("Could not encode item: {}", message))
                .orElseThrow(() -> new IllegalArgumentException("Could not convert the item to JSON"));
    }

    private static ItemStack decodeStack(JsonElement element) {
        RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, server.getRegistryManager());
        return ItemStack.CODEC.parse(ops, element)
                .resultOrPartial(message -> LOGGER.warn("Could not decode item: {}", message))
                .orElse(ItemStack.EMPTY);
    }

    private static void installDefaultGui() {
        GuiDefinition gui = new GuiDefinition("example");
        PageDefinition page = new PageDefinition(0, "GUI Maker Example", 3);

        ItemStack confirm = named(new ItemStack(Items.LIME_DYE), "Give example item", Formatting.GREEN);
        page.putButton(11, new ButtonDefinition(confirm, ButtonAction.GIVE_ITEM, "preset.confirm_item"));

        ItemStack sound = named(new ItemStack(Items.NOTE_BLOCK), "Click sound", Formatting.YELLOW);
        page.putButton(13, new ButtonDefinition(sound, ButtonAction.PLAY_SOUND, "ui.button.click"));

        ItemStack close = named(new ItemStack(Items.BARRIER), "Close menu", Formatting.RED);
        page.putButton(15, new ButtonDefinition(close, ButtonAction.CLOSE_GUI, ""));

        ItemStack command = named(new ItemStack(Items.COMMAND_BLOCK),
                "Run gated help command", Formatting.AQUA);
        page.putButton(22, new ButtonDefinition(command, ButtonAction.RUN_COMMAND, "show_help"));

        gui.addPage(page);
        GUIS.put(gui.id(), gui);
    }

    private static ItemStack named(ItemStack stack, String name, Formatting color) {
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name).formatted(color));
        return stack;
    }

    private static void backupBrokenConfig() {
        if (!Files.exists(dataFile())) {
            return;
        }
        try {
            String timestamp = Instant.now().toString().replace(':', '-');
            Files.move(dataFile(), dataFile().resolveSibling("guis.broken-" + timestamp + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception backupError) {
            LOGGER.error("Could not back up the invalid configuration.", backupError);
        }
    }
}
