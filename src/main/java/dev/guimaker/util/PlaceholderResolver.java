package dev.guimaker.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves trusted, server-provided placeholders for dialogs and commands. */
public final class PlaceholderResolver {
    private static final Gson GSON = new Gson();
    private static final Pattern KEY_PLACEHOLDER = Pattern.compile("\\{key:([A-Za-z0-9_]{1,32})}");

    private PlaceholderResolver() {}

    public static String resolve(String input, ServerPlayerEntity player) {
        return replace(input, values(player, false));
    }

    public static String resolveCommand(String input, ServerPlayerEntity player) {
        return resolveCommand(input, player, Map.of());
    }

    public static String resolveCommand(String input, ServerPlayerEntity player,
                                        Map<String, String> keyValues) {
        String result = input;
        for (Map.Entry<String, String> entry : values(player, false).entrySet()) {
            result = replaceCommandPlaceholder(result, entry.getKey(), entry.getValue());
        }
        return resolveKeyValues(result, keyValues);
    }

    /**
     * Replaces dialog input placeholders according to their command context:
     * JSON-string content is escaped without surrounding quotes, structured
     * JSON/SNBT values use a complete Gson string literal, and ordinary
     * Brigadier arguments use Brigadier quoting.
     */
    public static String resolveKeyValues(String input, Map<String, String> keyValues) {
        if (input == null || keyValues.isEmpty() || input.indexOf("{key:") < 0) {
            return input;
        }
        Matcher matcher = KEY_PLACEHOLDER.matcher(input);
        StringBuilder output = new StringBuilder(input.length());
        int end = 0;
        while (matcher.find()) {
            output.append(input, end, matcher.start());
            String key = matcher.group(1);
            String value = keyValues.get(key);
            if (value == null) {
                output.append(matcher.group());
            } else {
                output.append(encodeForContext(value, contextAt(input, matcher.start())));
            }
            end = matcher.end();
        }
        return output.append(input, end, input.length()).toString();
    }

    public static Set<String> keyPlaceholders(String input) {
        Set<String> keys = new LinkedHashSet<>();
        if (input == null) return keys;
        Matcher matcher = KEY_PLACEHOLDER.matcher(input);
        while (matcher.find()) keys.add(matcher.group(1));
        return keys;
    }

    public static JsonElement resolveJson(JsonElement element, ServerPlayerEntity player) {
        if (element == null || element.isJsonNull()) {
            return element;
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            return primitive.isString()
                    ? new JsonPrimitive(resolve(primitive.getAsString(), player))
                    : primitive.deepCopy();
        }
        if (element.isJsonArray()) {
            JsonArray result = new JsonArray();
            for (JsonElement child : element.getAsJsonArray()) {
                result.add(resolveJson(child, player));
            }
            return result;
        }
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            result.add(entry.getKey(), resolveJson(entry.getValue(), player));
        }
        return result;
    }

    public static String supportedPlaceholders() {
        return String.join(", ", new String[]{
                "{player}", "{player_name}", "{display_name}", "{uuid}",
                "{world}", "{dimension}", "{x}", "{y}", "{z}",
                "{block_x}", "{block_y}", "{block_z}", "{yaw}", "{pitch}",
                "{health}", "{max_health}", "{food}", "{level}", "{gamemode}",
                "{time}", "{server_players}", "{server_max_players}", "{server_motd}",
                "{key:<input_key>}"
        });
    }

    private static Map<String, String> values(ServerPlayerEntity player, boolean commandSafe) {
        MinecraftServer server = player.getServer();
        String playerName = player.getGameProfile().getName();
        String displayName = player.getDisplayName().getString();
        if (commandSafe) {
            playerName = StringArgumentType.escapeIfRequired(playerName);
            displayName = StringArgumentType.escapeIfRequired(displayName);
        }

        Map<String, String> values = new LinkedHashMap<>();
        values.put("{player}", playerName);
        values.put("{player_name}", playerName);
        values.put("{display_name}", displayName);
        values.put("{uuid}", player.getUuidAsString());
        values.put("{world}", player.getWorld().getRegistryKey().getValue().toString());
        values.put("{dimension}", player.getWorld().getRegistryKey().getValue().toString());
        values.put("{x}", decimal(player.getX()));
        values.put("{y}", decimal(player.getY()));
        values.put("{z}", decimal(player.getZ()));
        values.put("{block_x}", Integer.toString(player.getBlockX()));
        values.put("{block_y}", Integer.toString(player.getBlockY()));
        values.put("{block_z}", Integer.toString(player.getBlockZ()));
        values.put("{yaw}", decimal(player.getYaw()));
        values.put("{pitch}", decimal(player.getPitch()));
        values.put("{health}", decimal(player.getHealth()));
        values.put("{max_health}", decimal(player.getMaxHealth()));
        values.put("{food}", Integer.toString(player.getHungerManager().getFoodLevel()));
        values.put("{level}", Integer.toString(player.experienceLevel));
        values.put("{gamemode}", player.getGameMode().asString());
        values.put("{time}", Long.toString(player.getWorld().getTimeOfDay()));
        values.put("{server_players}", server == null ? "0" : Integer.toString(server.getCurrentPlayerCount()));
        values.put("{server_max_players}", server == null ? "0" : Integer.toString(server.getMaxPlayerCount()));
        String motd = server == null ? "" : server.getServerMotd();
        values.put("{server_motd}", commandSafe ? StringArgumentType.escapeIfRequired(motd) : motd);
        return values;
    }

    private static String replaceCommandPlaceholder(String input, String placeholder, String value) {
        int start = input.indexOf(placeholder);
        while (start >= 0) {
            String encoded = encodeForContext(value, contextAt(input, start));
            input = input.substring(0, start) + encoded + input.substring(start + placeholder.length());
            start = input.indexOf(placeholder, start + encoded.length());
        }
        return input;
    }

    private static String encodeForContext(String value, KeyContext context) {
        String jsonLiteral = GSON.toJson(value == null ? "" : value);
        return switch (context) {
            case JSON_STRING -> jsonLiteral.substring(1, jsonLiteral.length() - 1);
            case STRUCTURED_VALUE -> jsonLiteral;
            case COMMAND_ARGUMENT -> StringArgumentType.escapeIfRequired(value == null ? "" : value);
        };
    }

    private static KeyContext contextAt(String input, int limit) {
        boolean quoted = false;
        boolean escaped = false;
        int structuredDepth = 0;
        for (int index = 0; index < limit; index++) {
            char character = input.charAt(index);
            if (quoted) {
                if (escaped) {
                    escaped = false;
                } else if (character == '\\') {
                    escaped = true;
                } else if (character == '"') {
                    quoted = false;
                }
                continue;
            }
            if (character == '"') {
                quoted = true;
            } else if (character == '{' || character == '[') {
                // Placeholder braces are not JSON/SNBT structure braces.
                if (character == '{') {
                    int close = input.indexOf('}', index + 1);
                    if (close >= 0 && close < limit) {
                        String candidate = input.substring(index + 1, close);
                        if (candidate.matches("[A-Za-z_][A-Za-z0-9_:<>.-]*")) {
                            index = close;
                            continue;
                        }
                    }
                }
                structuredDepth++;
            } else if ((character == '}' || character == ']') && structuredDepth > 0) {
                structuredDepth--;
            }
        }
        if (quoted) return KeyContext.JSON_STRING;
        return structuredDepth > 0 ? KeyContext.STRUCTURED_VALUE : KeyContext.COMMAND_ARGUMENT;
    }

    private enum KeyContext {
        JSON_STRING,
        STRUCTURED_VALUE,
        COMMAND_ARGUMENT
    }

    private static String replace(String input, Map<String, String> values) {
        if (input == null || input.indexOf('{') < 0) {
            return input;
        }
        String result = input;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static String decimal(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
