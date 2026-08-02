package dev.guimaker.widget;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Immutable configuration for a chest-GUI widget slot. */
public record WidgetDefinition(
        Type type,
        Scope scope,
        String key,
        boolean defaultToggle,
        List<String> options,
        int minimum,
        int maximum,
        int step,
        int defaultCounter
) {
    public enum Type { TOGGLE, CYCLE, COUNTER, ITEM_HOLDER }
    public enum Scope { PLAYER, WORLD }

    public WidgetDefinition {
        if (type == null || scope == null) throw new IllegalArgumentException("Widget type and scope are required.");
        key = normalizeKey(key);
        List<String> cleanedOptions = new ArrayList<>();
        if (options != null) {
            for (String option : options) {
                String value = option == null ? "" : option.replaceAll("\\p{Cntrl}", "").strip();
                if (value.isEmpty() || value.length() > 64)
                    throw new IllegalArgumentException("Cycle options must be 1-64 visible characters.");
                if (!cleanedOptions.contains(value)) cleanedOptions.add(value);
            }
        }
        if (cleanedOptions.size() > 32)
            throw new IllegalArgumentException("Cycle widgets support at most 32 options.");
        options = List.copyOf(cleanedOptions);
        if (type == Type.CYCLE && options.isEmpty()) throw new IllegalArgumentException("Cycle widgets need at least one option.");
        if (type == Type.COUNTER) {
            if (minimum > maximum) throw new IllegalArgumentException("Counter minimum cannot exceed maximum.");
            if (step < 1) throw new IllegalArgumentException("Counter step must be positive.");
            if (defaultCounter < minimum || defaultCounter > maximum)
                throw new IllegalArgumentException("Counter default must be inside its range.");
        }
    }

    public static WidgetDefinition toggle(Scope scope, String key, boolean initial) {
        return new WidgetDefinition(Type.TOGGLE, scope, key, initial, List.of(), 0, 1, 1, 0);
    }

    public static WidgetDefinition cycle(Scope scope, String key, List<String> options) {
        return new WidgetDefinition(Type.CYCLE, scope, key, false, options, 0, 0, 1, 0);
    }

    public static WidgetDefinition counter(Scope scope, String key, int min, int max, int step, int initial) {
        return new WidgetDefinition(Type.COUNTER, scope, key, false, List.of(), min, max, step, initial);
    }

    public static WidgetDefinition itemHolder(Scope scope, String key) {
        return new WidgetDefinition(Type.ITEM_HOLDER, scope, key, false, List.of(), 0, 0, 1, 0);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", type.name());
        object.addProperty("scope", scope.name());
        object.addProperty("key", key);
        switch (type) {
            case TOGGLE -> object.addProperty("default", defaultToggle);
            case CYCLE -> {
                JsonArray values = new JsonArray();
                options.forEach(values::add);
                object.add("options", values);
            }
            case COUNTER -> {
                object.addProperty("min", minimum);
                object.addProperty("max", maximum);
                object.addProperty("step", step);
                object.addProperty("default", defaultCounter);
            }
            case ITEM_HOLDER -> { }
        }
        return object;
    }

    public static WidgetDefinition fromJson(JsonObject object) {
        Type type = Type.valueOf(object.get("type").getAsString().toUpperCase(Locale.ROOT));
        Scope scope = Scope.valueOf(object.get("scope").getAsString().toUpperCase(Locale.ROOT));
        String key = object.get("key").getAsString();
        return switch (type) {
            case TOGGLE -> toggle(scope, key, object.has("default") && object.get("default").getAsBoolean());
            case CYCLE -> {
                List<String> options = new ArrayList<>();
                for (JsonElement value : object.getAsJsonArray("options")) options.add(value.getAsString());
                yield cycle(scope, key, options);
            }
            case COUNTER -> counter(scope, key,
                    object.get("min").getAsInt(), object.get("max").getAsInt(),
                    object.has("step") ? object.get("step").getAsInt() : 1,
                    object.get("default").getAsInt());
            case ITEM_HOLDER -> itemHolder(scope, key);
        };
    }

    public static Scope parseScope(String raw) {
        try { return Scope.valueOf(raw.toUpperCase(Locale.ROOT)); }
        catch (Exception error) { throw new IllegalArgumentException("Scope must be player or world."); }
    }

    private static String normalizeKey(String raw) {
        String key = raw == null ? "" : raw.toLowerCase(Locale.ROOT).strip();
        if (!key.matches("[a-z0-9_.-]{1,64}"))
            throw new IllegalArgumentException("Widget key must use 1-64 lowercase letters, digits, _, . or -.");
        return key;
    }
}
