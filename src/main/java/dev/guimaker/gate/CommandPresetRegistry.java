package dev.guimaker.gate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.guimaker.config.LimitsConfig;
import dev.guimaker.config.WorldDataPaths;
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
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Server-owned allowlist of command presets. GUI actions store only a preset
 * ID; raw command text is never accepted from an in-game command or a player.
 */
public final class CommandPresetRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9_.-]{1,64}");
    private static final Pattern CONTROL_CHARACTER = Pattern.compile("[\\p{Cntrl}]");
    private static final Set<String> SENSITIVE_COMMANDS = Set.of(
            "op", "deop", "stop", "ban", "ban-ip", "pardon", "pardon-ip",
            "whitelist", "kick", "reload", "save-off", "save-all");
    private static final Pattern COMMAND_AFTER_RUN = Pattern.compile("(?:^|\\brun\\s+)([a-z0-9_:-]+)\\b");
    private static final int MAX_COMMAND_LENGTH = 512;
    private static final long MIN_COOLDOWN_MS = 250L;
    private static final long MAX_COOLDOWN_MS = 3_600_000L;
    private static final TreeMap<String, CommandPreset> PRESETS = new TreeMap<>();

    private CommandPresetRegistry() {}

    public enum ExecutionMode {
        PLAYER,
        SERVER
    }

    public record CommandPreset(
            String id,
            String command,
            ExecutionMode runAs,
            int serverPermissionLevel,
            int requiredPlayerPermissionLevel,
            long cooldownMs,
            boolean silent,
            boolean securityAcknowledged
    ) {}

    public static synchronized void load() {
        PRESETS.clear();
        CommandExecutionGate.clearAll();

        if (!Files.exists(configFile())) {
            installDefaults();
            save();
            LOGGER.info("GUI Maker created the command preset allowlist: {}", configFile());
            return;
        }

        try (Reader reader = Files.newBufferedReader(configFile(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray commands = root.getAsJsonArray("commands");
            if (commands == null) {
                throw new IllegalArgumentException("The 'commands' array is missing");
            }
            for (JsonElement element : commands) {
                try {
                    CommandPreset preset = decode(element.getAsJsonObject());
                    auditSecurity(preset);
                    if (PRESETS.size() >= LimitsConfig.maxCommandPresets()) {
                        LOGGER.warn("Command preset limit ({}) reached; remaining entries were skipped.",
                                LimitsConfig.maxCommandPresets());
                        break;
                    }
                    if (PRESETS.putIfAbsent(preset.id(), preset) != null) {
                        LOGGER.warn("Duplicate command preset ID skipped: {}", preset.id());
                    }
                } catch (Exception entryError) {
                    LOGGER.warn("Invalid command preset skipped: {}", entryError.getMessage());
                }
            }
            if (PRESETS.isEmpty()) {
                installDefaults();
                save();
            }
            LOGGER.info("GUI Maker loaded {} command preset(s).", PRESETS.size());
        } catch (Exception error) {
            LOGGER.error("Could not read the command preset allowlist; the invalid file will be backed up.", error);
            backupBrokenConfig();
            PRESETS.clear();
            installDefaults();
            save();
        }
    }

    public static synchronized void clear() {
        PRESETS.clear();
        CommandExecutionGate.clearAll();
    }

    public static synchronized boolean isRegistered(String rawId) {
        return rawId != null && PRESETS.containsKey(rawId.toLowerCase(Locale.ROOT));
    }

    public static synchronized CommandPreset get(String rawId) {
        if (rawId == null) {
            return null;
        }
        return PRESETS.get(rawId.toLowerCase(Locale.ROOT));
    }

    public static synchronized Collection<String> ids() {
        return Collections.unmodifiableList(PRESETS.keySet().stream().toList());
    }

    public static synchronized Collection<CommandPreset> all() {
        return Collections.unmodifiableList(PRESETS.values().stream().toList());
    }

    public static boolean requiresSecurityAcknowledgement(CommandPreset preset) {
        return preset != null
                && preset.runAs() == ExecutionMode.SERVER
                && (preset.serverPermissionLevel() >= 3 || sensitiveCommand(preset.command()).isPresent());
    }

    private static void auditSecurity(CommandPreset preset) {
        if (preset.runAs() != ExecutionMode.SERVER) return;
        Optional<String> sensitive = sensitiveCommand(preset.command());
        if (preset.serverPermissionLevel() >= 3) {
            LOGGER.warn("SECURITY: preset '{}' requests SERVER permission level {} (acknowledged={}).",
                    preset.id(), preset.serverPermissionLevel(), preset.securityAcknowledged());
        }
        sensitive.ifPresent(command -> LOGGER.warn(
                "SECURITY: preset '{}' contains sensitive command '{}' (acknowledged={}): /{}",
                preset.id(), command, preset.securityAcknowledged(), preset.command()));
        if (requiresSecurityAcknowledgement(preset) && !preset.securityAcknowledged()) {
            LOGGER.warn("SECURITY GATE: preset '{}' remains loaded but execution is blocked until "
                    + "security_acknowledged is set to true in commands.json.", preset.id());
        }
    }

    private static Optional<String> sensitiveCommand(String command) {
        Matcher matcher = COMMAND_AFTER_RUN.matcher(command.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            String root = matcher.group(1);
            int namespaceSeparator = root.indexOf(':');
            if (namespaceSeparator >= 0) root = root.substring(namespaceSeparator + 1);
            if (SENSITIVE_COMMANDS.contains(root)) return Optional.of(root);
        }
        return Optional.empty();
    }

    private static Path configFile() {
        return WorldDataPaths.file("commands.json");
    }

    private static CommandPreset decode(JsonObject object) {
        String id = validateId(requiredString(object, "id"));
        String command = validateCommand(requiredString(object, "command"));
        ExecutionMode runAs = ExecutionMode.valueOf(optionalString(object, "run_as", "PLAYER")
                .toUpperCase(Locale.ROOT));
        int permissionLevel = optionalInt(object, "server_permission_level", 2);
        int requiredPermission = optionalInt(object, "required_player_permission_level", 0);
        long cooldownMs = optionalLong(object, "cooldown_ms", 1000L);
        boolean silent = optionalBoolean(object, "silent", false);
        boolean securityAcknowledged = optionalBoolean(object, "security_acknowledged", false);

        validatePermissionLevel(permissionLevel, "server_permission_level");
        validatePermissionLevel(requiredPermission, "required_player_permission_level");
        if (cooldownMs < MIN_COOLDOWN_MS || cooldownMs > MAX_COOLDOWN_MS) {
            throw new IllegalArgumentException("cooldown_ms must be between "
                    + MIN_COOLDOWN_MS + " and " + MAX_COOLDOWN_MS);
        }
        return new CommandPreset(id, command, runAs, permissionLevel,
                requiredPermission, cooldownMs, silent, securityAcknowledged);
    }

    private static synchronized void save() {
        try {
            Files.createDirectories(configFile().getParent());
            JsonObject root = new JsonObject();
            root.addProperty("format", 1);
            JsonArray commands = new JsonArray();
            for (CommandPreset preset : PRESETS.values()) {
                JsonObject object = new JsonObject();
                object.addProperty("id", preset.id());
                object.addProperty("command", preset.command());
                object.addProperty("run_as", preset.runAs().name());
                object.addProperty("server_permission_level", preset.serverPermissionLevel());
                object.addProperty("required_player_permission_level", preset.requiredPlayerPermissionLevel());
                object.addProperty("cooldown_ms", preset.cooldownMs());
                object.addProperty("silent", preset.silent());
                object.addProperty("security_acknowledged", preset.securityAcknowledged());
                commands.add(object);
            }
            root.add("commands", commands);

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
            LOGGER.error("Could not save the command preset allowlist.", error);
        }
    }

    private static void installDefaults() {
        putDefault(new CommandPreset(
                "show_help",
                "help",
                ExecutionMode.PLAYER,
                0,
                0,
                1000L,
                false,
                false
        ));
        putDefault(new CommandPreset(
                "welcome_message",
                "tellraw {player} {\"text\":\"Welcome!\",\"color\":\"green\"}",
                ExecutionMode.SERVER,
                2,
                0,
                1000L,
                true,
                false
        ));
    }

    private static void putDefault(CommandPreset preset) {
        PRESETS.put(preset.id(), preset);
    }

    private static String validateId(String raw) {
        String id = raw.toLowerCase(Locale.ROOT).strip();
        if (!VALID_ID.matcher(id).matches()) {
            throw new IllegalArgumentException("Preset ID must be 1-64 characters and use only a-z, 0-9, _, . and -");
        }
        return id;
    }

    private static String validateCommand(String raw) {
        String command = raw.strip();
        while (command.startsWith("/")) {
            command = command.substring(1).stripLeading();
        }
        if (command.isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }
        if (command.length() > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException("Command exceeds " + MAX_COMMAND_LENGTH + " characters");
        }
        if (CONTROL_CHARACTER.matcher(command).find()) {
            throw new IllegalArgumentException("Command contains a control character");
        }
        return command;
    }

    private static void validatePermissionLevel(int value, String field) {
        if (value < 0 || value > 4) {
            throw new IllegalArgumentException(field + " must be between 0 and 4");
        }
    }

    private static String requiredString(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) {
            throw new IllegalArgumentException("Missing string field: " + key);
        }
        return object.get(key).getAsString();
    }

    private static String optionalString(JsonObject object, String key, String fallback) {
        return object.has(key) ? object.get(key).getAsString() : fallback;
    }

    private static int optionalInt(JsonObject object, String key, int fallback) {
        return object.has(key) ? object.get(key).getAsInt() : fallback;
    }

    private static long optionalLong(JsonObject object, String key, long fallback) {
        return object.has(key) ? object.get(key).getAsLong() : fallback;
    }

    private static boolean optionalBoolean(JsonObject object, String key, boolean fallback) {
        return object.has(key) ? object.get(key).getAsBoolean() : fallback;
    }

    private static void backupBrokenConfig() {
        if (!Files.exists(configFile())) {
            return;
        }
        try {
            String timestamp = Instant.now().toString().replace(':', '-');
            Files.move(configFile(), configFile().resolveSibling("commands.broken-" + timestamp + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception backupError) {
            LOGGER.error("Could not back up the invalid command preset allowlist.", backupError);
        }
    }
}
