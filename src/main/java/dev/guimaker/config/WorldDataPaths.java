package dev.guimaker.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;

/** Resolves GUI Maker data inside the active save and migrates legacy globals. */
public final class WorldDataPaths {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");
    private static final List<String> DATA_FILES = List.of(
            "guis.json", "dialogs.json", "commands.json", "limits.json", "widget-state.json");
    private static Path worldDirectory;

    private WorldDataPaths() {}

    public static synchronized void initialize(MinecraftServer server) {
        worldDirectory = server.getSavePath(WorldSavePath.ROOT)
                .resolve("data").resolve("guimaker").toAbsolutePath().normalize();
        try {
            Files.createDirectories(worldDirectory);
            migrateLegacyGlobalFiles();
        } catch (Exception error) {
            throw new IllegalStateException("Could not initialize world-specific GUI Maker data", error);
        }
        LOGGER.info("GUI Maker world data directory: {}", worldDirectory);
    }

    public static synchronized Path file(String name) {
        if (worldDirectory == null) {
            throw new IllegalStateException("WorldDataPaths has not been initialized");
        }
        if (!DATA_FILES.contains(name)) {
            throw new IllegalArgumentException("Unknown GUI Maker world data file: " + name);
        }
        return worldDirectory.resolve(name);
    }

    public static synchronized Path directory() {
        if (worldDirectory == null) {
            throw new IllegalStateException("WorldDataPaths has not been initialized");
        }
        return worldDirectory;
    }

    public static synchronized void clear() {
        worldDirectory = null;
    }

    private static void migrateLegacyGlobalFiles() throws Exception {
        Path legacyDirectory = FabricLoader.getInstance().getConfigDir().resolve("guimaker");
        JsonArray copied = new JsonArray();
        for (String fileName : DATA_FILES) {
            Path source = legacyDirectory.resolve(fileName);
            Path target = worldDirectory.resolve(fileName);
            if (copyLegacyFileIfAbsent(source, target)) {
                copied.add(fileName);
                LOGGER.info("Copied legacy global GUI Maker data into this world: {} -> {}", source, target);
            }
        }
        if (!copied.isEmpty()) {
            // Legacy files are intentionally copied, never moved or deleted, so
            // they remain an automatic backup and a template for other saves.
            JsonObject marker = new JsonObject();
            marker.addProperty("migration", 1);
            marker.addProperty("copied_at", Instant.now().toString());
            marker.addProperty("legacy_directory", legacyDirectory.toAbsolutePath().toString());
            marker.add("copied_files", copied);
            try (Writer writer = Files.newBufferedWriter(
                    worldDirectory.resolve("migration.json"), StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(marker, writer);
            }
        }
    }

    static boolean copyLegacyFileIfAbsent(Path source, Path target) throws IOException {
        if (!Files.isRegularFile(source)) return false;
        try {
            // Files.copy without REPLACE_EXISTING has CREATE_NEW semantics.
            // Exactly one concurrent caller can create the target.
            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
            return true;
        } catch (FileAlreadyExistsException ignored) {
            LOGGER.debug("World GUI Maker data already exists; migration skipped: {}", target);
            return false;
        }
    }
}
