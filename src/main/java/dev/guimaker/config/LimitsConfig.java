package dev.guimaker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

/** Loads configurable capacity limits from the active world's data directory. */
public final class LimitsConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int DEFAULT_MAX_GUIS = 512;
    private static final int DEFAULT_MAX_COMMAND_PRESETS = 1024;
    private static final int HARD_MAX_GUIS = 100_000;
    private static final int HARD_MAX_COMMAND_PRESETS = 100_000;

    private static int maxGuis = DEFAULT_MAX_GUIS;
    private static int maxCommandPresets = DEFAULT_MAX_COMMAND_PRESETS;

    private LimitsConfig() {}

    public static synchronized void load() {
        maxGuis = DEFAULT_MAX_GUIS;
        maxCommandPresets = DEFAULT_MAX_COMMAND_PRESETS;

        if (!Files.exists(configFile())) {
            save();
            LOGGER.info("GUI Maker created the limits configuration: {}", configFile());
            return;
        }

        try (Reader reader = Files.newBufferedReader(configFile(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            maxGuis = readLimit(root, "max_guis", DEFAULT_MAX_GUIS, HARD_MAX_GUIS);
            maxCommandPresets = readLimit(root, "max_command_presets",
                    DEFAULT_MAX_COMMAND_PRESETS, HARD_MAX_COMMAND_PRESETS);
            LOGGER.info("GUI Maker limits loaded: max_guis={}, max_command_presets={}.",
                    maxGuis, maxCommandPresets);
        } catch (Exception error) {
            LOGGER.error("Could not read limits.json; the invalid file will be backed up.", error);
            backupBrokenConfig();
            maxGuis = DEFAULT_MAX_GUIS;
            maxCommandPresets = DEFAULT_MAX_COMMAND_PRESETS;
            save();
        }
    }

    public static int maxGuis() {
        return maxGuis;
    }

    public static int maxCommandPresets() {
        return maxCommandPresets;
    }

    private static Path configFile() {
        return WorldDataPaths.file("limits.json");
    }

    private static int readLimit(JsonObject root, String key, int fallback, int hardMaximum) {
        if (!root.has(key)) {
            return fallback;
        }
        int value = root.get(key).getAsInt();
        if (value < 1 || value > hardMaximum) {
            throw new IllegalArgumentException(key + " must be between 1 and " + hardMaximum);
        }
        return value;
    }

    private static void save() {
        try {
            Files.createDirectories(configFile().getParent());
            JsonObject root = new JsonObject();
            root.addProperty("format", 1);
            root.addProperty("max_guis", maxGuis);
            root.addProperty("max_command_presets", maxCommandPresets);

            Path temporary = configFile().resolveSibling(configFile().getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            try {
                Files.move(temporary, configFile(), StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, configFile(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception error) {
            LOGGER.error("Could not save the GUI Maker limits configuration.", error);
        }
    }

    private static void backupBrokenConfig() {
        if (!Files.exists(configFile())) {
            return;
        }
        try {
            String timestamp = Instant.now().toString().replace(':', '-');
            Files.move(configFile(), configFile().resolveSibling("limits.broken-" + timestamp + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception backupError) {
            LOGGER.error("Could not back up the invalid limits configuration.", backupError);
        }
    }
}
