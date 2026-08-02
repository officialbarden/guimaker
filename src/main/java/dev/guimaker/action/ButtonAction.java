package dev.guimaker.action;

/**
 * ButtonAction — Closed, fixed list of allowed GUI button action types.
 *
 * WHY THE ORIGINAL DATAPACK WAS RISKY:
 * In data/gm/function/zprivate/execution/run_function/main.mcfunction, the
 * "function" field that a player typed into the GUI editor was executed
 * directly with no validation ($function $(function_file) ...). This meant
 * any player could trigger ANY function on the server, including privileged
 * ones. The "command" field had the same pattern.
 *
 * HOW THIS ENUM FIXES IT:
 * In Java, an enum is a fixed, closed set of values — new members cannot be
 * added at runtime. This means a GUI button can ONLY perform one of the
 * actions defined here. An arbitrary string ("any function name") is never
 * executed directly; only one of these enum values can ever be selected.
 */
public enum ButtonAction {
    // Switches the GUI to a different page/screen when clicked.
    OPEN_PAGE,

    // Closes the GUI when clicked.
    CLOSE_GUI,

    // Plays a sound on click (from a fixed, predefined sound list).
    PLAY_SOUND,

    // Gives the player a fixed, predefined item on click.
    GIVE_ITEM,

    // Executes a server-owned command preset after permission and cooldown gates.
    // The parameter is a preset ID, never raw command text.
    RUN_COMMAND,

    // Does nothing on click (decorative/visual-only button).
    NO_OP;

    /**
     * Safely converts a string (e.g. from JSON data) into a ButtonAction.
     * Returns NO_OP if there's no match — it never tries to "guess and run"
     * an unknown action. This is the direct opposite of the original
     * datapack's "whatever you type gets executed" behavior.
     */
    public static ButtonAction fromSafeString(String raw) {
        if (raw == null) {
            return NO_OP;
        }
        for (ButtonAction action : values()) {
            if (action.name().equalsIgnoreCase(raw)) {
                return action;
            }
        }
        // Unrecognized value -> safe default. Never throws an exception that
        // could crash the server, never attempts to execute an unknown string.
        return NO_OP;
    }
}
