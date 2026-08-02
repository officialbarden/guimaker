package dev.guimaker.gate;

import dev.guimaker.action.ButtonAction;

/**
 * ActionAllowlistGate — GATE #2: Action allowlist validation.
 *
 * THE ORIGINAL DATAPACK'S MAIN VULNERABILITY:
 * data/gm/function/zprivate/execution/run_function/main.mcfunction:
 *   $execute if data storage gm:storage temp.macro.function_file as $(player) at @s
 *     run function gm:zprivate/execution/run_function/macro with storage gm:storage temp.macro
 *
 *   And inside macro.mcfunction:
 *   $function $(function_file) with storage gm:storage temp.macro
 *
 * Here, $(function_file) — any string a player typed into an item via the
 * GUI editor — was passed directly to the $function command with no
 * validation. This meant ANY function (in "namespace:path" format),
 * including unauthorized ones, could be executed. The "command" field had
 * the same pattern.
 *
 * HOW THIS CLASS PREVENTS IT:
 * Every button carries a value from the ButtonAction enum (a closed list).
 * Command actions contain only an ID from the server-owned command preset
 * registry; raw command text is never accepted from a GUI or player command.
 * This gate verifies that the action and its parameter are allowlisted before
 * the dispatcher can perform any operation.
 */
public final class ActionAllowlistGate {

    // Reasonable upper bound for GUI page IDs (to prevent resource
    // exhaustion or index overflow from unbounded/excessively large indices).
    private static final int MAX_PAGE_ID = 9999;

    private ActionAllowlistGate() {}

    /**
     * Checks whether a ButtonAction and its accompanying parameter are
     * safe to execute. Returns false to mean the action is silently
     * treated as NO_OP — never "try the unknown anyway."
     */
    public static boolean isAllowed(ButtonAction action, String rawParam) {
        switch (action) {
            case OPEN_PAGE:
                return isValidPageReference(rawParam);
            case CLOSE_GUI:
            case NO_OP:
                // Requires no parameter, always safe.
                return true;
            case PLAY_SOUND:
                return isKnownSoundId(rawParam);
            case GIVE_ITEM:
                return isKnownItemPresetId(rawParam);
            case RUN_COMMAND:
                return isKnownCommandPresetId(rawParam);
            default:
                // If a new value is ever added to the enum and not handled
                // here, DENY by default (deny-by-default).
                return false;
        }
    }

    // The OPEN_PAGE parameter can only be a numeric page ID — never a file
    // path or a namespace:path format.
    private static boolean isValidPageReference(String rawParam) {
        if (rawParam == null || rawParam.isEmpty()) {
            return false;
        }
        try {
            int pageId = Integer.parseInt(rawParam.trim());
            return pageId >= 0 && pageId <= MAX_PAGE_ID;
        } catch (NumberFormatException e) {
            // Not a number -> rejected immediately; no interpretation attempted.
            return false;
        }
    }

    // Sounds must come from a fixed in-mod registry (SoundRegistry);
    // any string typed by a player is never accepted.
    private static boolean isKnownSoundId(String rawParam) {
        return dev.guimaker.gate.SoundRegistry.isRegistered(rawParam);
    }

    // Item presets likewise come from a fixed registry.
    private static boolean isKnownItemPresetId(String rawParam) {
        return ItemPresetRegistry.isRegistered(rawParam);
    }

    // Command text never appears here. Only a server-owned preset ID is valid.
    private static boolean isKnownCommandPresetId(String rawParam) {
        return CommandPresetRegistry.isRegistered(rawParam);
    }
}
