package dev.guimaker.gate;

import java.util.Set;

/**
 * ItemPresetRegistry — Fixed list of items GUI buttons are allowed to give.
 *
 * IN THE ORIGINAL DATAPACK:
 * The "item_modifier" field was a free string written by the player, later
 * executed directly as "$item modify ... $(ItemModifier)"
 * (data/gm/function/zprivate/prerequisites/item_modifier/macro.mcfunction).
 * This meant item modifier functions could be invoked arbitrarily.
 *
 * WHAT'S DIFFERENT HERE:
 * Only preset IDs defined here are accepted. Each preset ID corresponds to
 * a fixed ItemStack construction in mod code — the player's NBT/modifier
 * string is never processed directly.
 */
public final class ItemPresetRegistry {

    private static final Set<String> ALLOWED_PRESETS = Set.of(
            "preset.empty_slot_filler",
            "preset.back_button_item",
            "preset.confirm_item",
            "preset.cancel_item"
    );

    private ItemPresetRegistry() {}

    public static boolean isRegistered(String presetId) {
        return presetId != null && ALLOWED_PRESETS.contains(presetId);
    }

    public static java.util.Set<String> ids() {
        return java.util.Collections.unmodifiableSet(ALLOWED_PRESETS);
    }
}
