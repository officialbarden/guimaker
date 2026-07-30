package dev.guimaker.util;

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
        String result = replace(input, values(player, true));
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            String safeValue = StringArgumentType.escapeIfRequired(entry.getValue());
            result = result.replace("{key:" + entry.getKey() + "}", safeValue);
        }
        return result;
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
