package com.runtoolkit.guimaker.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.runtoolkit.guimaker.action.ButtonAction;
import com.runtoolkit.guimaker.gate.ActionAllowlistGate;
import net.fabricmc.loader.api.FabricLoader;
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
 * config/guimaker/guis.json using an atomic replace where supported.
 */
public final class GuiRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final int FORMAT_VERSION = 1;
    private static final int MAX_GUIS = 128;
    private static final TreeMap<String, GuiDefinition> GUIS = new TreeMap<>();
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("guimaker").resolve("guis.json");

    private static MinecraftServer server;

    private GuiRepository() {}

    public static synchronized void load(MinecraftServer minecraftServer) {
        server = minecraftServer;
        GUIS.clear();

        if (!Files.exists(CONFIG_FILE)) {
            installDefaultGui();
            save();
            LOGGER.info("GUI Maker varsayılan yapılandırmayı oluşturdu: {}", CONFIG_FILE);
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray guis = root.getAsJsonArray("guis");
            if (guis == null) {
                throw new IllegalArgumentException("'guis' dizisi bulunamadı");
            }
            for (JsonElement element : guis) {
                try {
                    GuiDefinition gui = decodeGui(element.getAsJsonObject());
                    if (GUIS.size() < MAX_GUIS) {
                        GUIS.put(gui.id(), gui);
                    }
                } catch (Exception entryError) {
                    LOGGER.warn("Geçersiz GUI kaydı atlandı: {}", entryError.getMessage());
                }
            }

            if (GUIS.isEmpty()) {
                installDefaultGui();
                save();
            }
            LOGGER.info("GUI Maker {} GUI yükledi.", GUIS.size());
        } catch (Exception error) {
            LOGGER.error("GUI Maker yapılandırması okunamadı; bozuk dosya yedeklenecek.", error);
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
            throw new IllegalArgumentException("GUI bulunamadı: " + id);
        }
        return gui;
    }

    public static synchronized PageDefinition requirePage(String rawGuiId, int pageId) {
        GuiDefinition gui = requireGui(rawGuiId);
        PageDefinition page = gui.page(pageId);
        if (page == null) {
            throw new IllegalArgumentException("Sayfa bulunamadı: " + pageId);
        }
        return page;
    }

    public static synchronized GuiDefinition create(String rawId, int rows, String title) {
        String id = GuiDefinition.normalizeAndValidateId(rawId);
        if (GUIS.containsKey(id)) {
            throw new IllegalArgumentException("Bu GUI zaten mevcut: " + id);
        }
        if (GUIS.size() >= MAX_GUIS) {
            throw new IllegalArgumentException("Sunucuda en fazla " + MAX_GUIS + " GUI oluşturulabilir.");
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
            throw new IllegalArgumentException("GUI bulunamadı: " + id);
        }
        if (GUIS.size() <= 1) {
            throw new IllegalArgumentException("Son GUI silinemez; önce başka bir GUI oluşturun.");
        }
        GUIS.remove(id);
        save();
    }

    public static synchronized void addPage(String guiId, int pageId, int rows, String title) {
        requireGui(guiId).addPage(new PageDefinition(pageId, title, rows));
        save();
    }

    public static synchronized void removePage(String guiId, int pageId) {
        requireGui(guiId).removePage(pageId);
        save();
    }

    public static synchronized void setItem(String guiId, int pageId, int slot, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Ana elinizde bir eşya tutmalısınız.");
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
            throw new IllegalArgumentException("Eylem veya parametre izin verilen listede değil.");
        }
        if (action == ButtonAction.OPEN_PAGE) {
            int targetPage = Integer.parseInt(safeParameter);
            if (!requireGui(guiId).hasPage(targetPage)) {
                throw new IllegalArgumentException("Hedef sayfa bulunamadı: " + targetPage);
            }
        }
        requirePage(guiId, pageId).setAction(slot, action, safeParameter);
        save();
    }

    public static synchronized void save() {
        if (server == null) {
            return;
        }
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("format", FORMAT_VERSION);
            JsonArray guis = new JsonArray();
            for (GuiDefinition gui : GUIS.values()) {
                guis.add(encodeGui(gui));
            }
            root.add("guis", guis);

            Path temporary = CONFIG_FILE.resolveSibling(CONFIG_FILE.getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            try {
                Files.move(temporary, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception error) {
            LOGGER.error("GUI Maker yapılandırması kaydedilemedi.", error);
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
            throw new IllegalArgumentException("GUI içinde sayfa yok");
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
                    page.putButton(slot, new ButtonDefinition(stack, action, parameter));
                }
            }
            gui.addPage(page);
        }
        return gui;
    }

    private static JsonElement encodeStack(ItemStack stack) {
        RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, server.getRegistryManager());
        return ItemStack.CODEC.encodeStart(ops, stack)
                .resultOrPartial(message -> LOGGER.warn("Eşya kodlanamadı: {}", message))
                .orElseThrow(() -> new IllegalArgumentException("Eşya JSON'a dönüştürülemedi"));
    }

    private static ItemStack decodeStack(JsonElement element) {
        RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, server.getRegistryManager());
        return ItemStack.CODEC.parse(ops, element)
                .resultOrPartial(message -> LOGGER.warn("Eşya çözülemedi: {}", message))
                .orElse(ItemStack.EMPTY);
    }

    private static void installDefaultGui() {
        GuiDefinition gui = new GuiDefinition("example");
        PageDefinition page = new PageDefinition(0, "GUI Maker Örneği", 3);

        ItemStack confirm = named(new ItemStack(Items.LIME_DYE), "Örnek eşya ver", Formatting.GREEN);
        page.putButton(11, new ButtonDefinition(confirm, ButtonAction.GIVE_ITEM, "preset.confirm_item"));

        ItemStack sound = named(new ItemStack(Items.NOTE_BLOCK), "Tıklama sesi", Formatting.YELLOW);
        page.putButton(13, new ButtonDefinition(sound, ButtonAction.PLAY_SOUND, "ui.button.click"));

        ItemStack close = named(new ItemStack(Items.BARRIER), "Menüyü kapat", Formatting.RED);
        page.putButton(15, new ButtonDefinition(close, ButtonAction.CLOSE_GUI, ""));

        gui.addPage(page);
        GUIS.put(gui.id(), gui);
    }

    private static ItemStack named(ItemStack stack, String name, Formatting color) {
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name).formatted(color));
        return stack;
    }

    private static void backupBrokenConfig() {
        if (!Files.exists(CONFIG_FILE)) {
            return;
        }
        try {
            String timestamp = Instant.now().toString().replace(':', '-');
            Files.move(CONFIG_FILE, CONFIG_FILE.resolveSibling("guis.broken-" + timestamp + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception backupError) {
            LOGGER.error("Bozuk yapılandırma yedeklenemedi.", backupError);
        }
    }
}
