package dev.guimaker.widget;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import dev.guimaker.config.WorldDataPaths;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Persistent player/world widget state with throttled atomic writes. */
public final class WidgetStateRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final long SAVE_DELAY_MS = 1000L;
    private static JsonObject root = emptyRoot();
    private static MinecraftServer server;
    private static boolean dirty;
    private static long dirtySince;
    private static final ConcurrentHashMap<String, UUID> HOLDER_LOCKS = new ConcurrentHashMap<>();

    private WidgetStateRepository() {}

    public static synchronized void load(MinecraftServer minecraftServer) {
        server = minecraftServer;
        root = emptyRoot();
        Path file = dataFile();
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonObject loaded = JsonParser.parseReader(reader).getAsJsonObject();
                if (loaded.has("world")) root.add("world", loaded.getAsJsonObject("world"));
                if (loaded.has("players")) root.add("players", loaded.getAsJsonObject("players"));
            } catch (Exception error) {
                LOGGER.error("Could not read widget-state.json; starting with empty widget state.", error);
            }
        } else {
            saveNow();
        }
        dirty = false;
        HOLDER_LOCKS.clear();
    }

    public static boolean acquireHolder(WidgetDefinition widget, ServerPlayerEntity player) {
        return tryAcquireHolderLock(holderLockKey(widget, player), player.getUuid());
    }

    public static void releaseHolder(WidgetDefinition widget, ServerPlayerEntity player) {
        releaseHolderLock(holderLockKey(widget, player), player.getUuid());
    }

    static boolean tryAcquireHolderLock(String lockKey, UUID owner) {
        UUID existing = HOLDER_LOCKS.putIfAbsent(lockKey, owner);
        return existing == null || existing.equals(owner);
    }

    static void releaseHolderLock(String lockKey, UUID owner) {
        HOLDER_LOCKS.remove(lockKey, owner);
    }

    static void clearHolderLocks() {
        HOLDER_LOCKS.clear();
    }

    public static synchronized boolean getToggle(WidgetDefinition widget, ServerPlayerEntity player) {
        JsonElement value = state(widget, player).get(stateKey(widget));
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()
                ? value.getAsBoolean() : widget.defaultToggle();
    }

    public static synchronized boolean toggle(WidgetDefinition widget, ServerPlayerEntity player) {
        boolean value = !getToggle(widget, player);
        state(widget, player).addProperty(stateKey(widget), value);
        markDirty();
        return value;
    }

    public static synchronized String getCycle(WidgetDefinition widget, ServerPlayerEntity player) {
        JsonElement value = state(widget, player).get(stateKey(widget));
        String selected = value != null && value.isJsonPrimitive() ? value.getAsString() : widget.options().getFirst();
        return widget.options().contains(selected) ? selected : widget.options().getFirst();
    }

    public static synchronized String cycle(WidgetDefinition widget, ServerPlayerEntity player) {
        String current = getCycle(widget, player);
        int index = (widget.options().indexOf(current) + 1) % widget.options().size();
        String value = widget.options().get(index);
        state(widget, player).addProperty(stateKey(widget), value);
        markDirty();
        return value;
    }

    public static synchronized int getCounter(WidgetDefinition widget, ServerPlayerEntity player) {
        JsonElement value = state(widget, player).get(stateKey(widget));
        int number = value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()
                ? value.getAsInt() : widget.defaultCounter();
        return Math.max(widget.minimum(), Math.min(widget.maximum(), number));
    }

    public static synchronized int changeCounter(WidgetDefinition widget, ServerPlayerEntity player, int direction) {
        long next = (long) getCounter(widget, player) + (long) widget.step() * Integer.signum(direction);
        int value = (int) Math.max(widget.minimum(), Math.min(widget.maximum(), next));
        state(widget, player).addProperty(stateKey(widget), value);
        markDirty();
        return value;
    }

    public static synchronized ItemStack getHolder(WidgetDefinition widget, ServerPlayerEntity player) {
        JsonElement value = state(widget, player).get(stateKey(widget));
        if (value == null || !value.isJsonObject() || server == null) return ItemStack.EMPTY;
        RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, server.getRegistryManager());
        return ItemStack.CODEC.parse(ops, value)
                .resultOrPartial(message -> LOGGER.warn("Could not decode holder '{}': {}", widget.key(), message))
                .map(ItemStack::copy).orElse(ItemStack.EMPTY);
    }

    public static synchronized void setHolder(WidgetDefinition widget, ServerPlayerEntity player, ItemStack stack) {
        JsonObject values = state(widget, player);
        if (stack == null || stack.isEmpty()) {
            values.remove(stateKey(widget));
        } else {
            RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, server.getRegistryManager());
            JsonElement encoded = ItemStack.CODEC.encodeStart(ops, stack.copy())
                    .resultOrPartial(message -> LOGGER.warn("Could not encode holder '{}': {}", widget.key(), message))
                    .orElseThrow(() -> new IllegalArgumentException("Could not encode holder item"));
            values.add(stateKey(widget), encoded);
        }
        markDirty();
    }

    public static synchronized void reset(WidgetDefinition widget, ServerPlayerEntity player) {
        state(widget, player).remove(stateKey(widget));
        markDirty();
    }

    public static synchronized void flushIfDue() {
        if (dirty && System.currentTimeMillis() - dirtySince >= SAVE_DELAY_MS) saveNow();
    }

    public static synchronized void close() {
        if (dirty) saveNow();
        root = emptyRoot();
        HOLDER_LOCKS.clear();
        server = null;
    }

    private static String stateKey(WidgetDefinition widget) {
        return widget.type().name().toLowerCase(java.util.Locale.ROOT) + ":" + widget.key();
    }

    private static String holderLockKey(WidgetDefinition widget, ServerPlayerEntity player) {
        return widget.scope() == WidgetDefinition.Scope.WORLD
                ? "world:" + widget.key()
                : "player:" + player.getUuidAsString() + ":" + widget.key();
    }

    private static JsonObject state(WidgetDefinition widget, ServerPlayerEntity player) {
        if (widget.scope() == WidgetDefinition.Scope.WORLD) return root.getAsJsonObject("world");
        if (player == null) throw new IllegalArgumentException("Player-scoped widget requires a player");
        JsonObject players = root.getAsJsonObject("players");
        String uuid = player.getUuidAsString();
        if (!players.has(uuid)) players.add(uuid, new JsonObject());
        return players.getAsJsonObject(uuid);
    }

    private static void markDirty() {
        if (!dirty) dirtySince = System.currentTimeMillis();
        dirty = true;
    }

    private static void saveNow() {
        if (server == null) return;
        try {
            Path file = dataFile();
            Files.createDirectories(file.getParent());
            JsonObject output = root.deepCopy();
            output.addProperty("format", 1);
            Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(output, writer);
            }
            try {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
            dirty = false;
        } catch (Exception error) {
            LOGGER.error("Could not save widget-state.json", error);
        }
    }

    private static JsonObject emptyRoot() {
        JsonObject object = new JsonObject();
        object.add("world", new JsonObject());
        object.add("players", new JsonObject());
        return object;
    }

    private static Path dataFile() {
        return WorldDataPaths.file("widget-state.json");
    }
}
