package com.runtoolkit.guimaker.gui;

import com.runtoolkit.guimaker.action.ButtonAction;
import com.runtoolkit.guimaker.gate.ActionAllowlistGate;
import com.runtoolkit.guimaker.gate.RateLimitGate;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * GuiButtonExecutor — The SINGLE entry point called when a player clicks a
 * GUI button. This is the counterpart to the original datapack's
 * "run_function/main.mcfunction", but instead of executing directly with
 * no gates, every request passes through three gates IN SEQUENCE here. If
 * any gate rejects it, the operation stops.
 *
 * GATE ORDER MATTERS:
 * 1) RateLimitGate       -> spam/performance protection (cheapest check, done first)
 * 2) ActionAllowlistGate -> validity of the action and its parameter
 * (PermissionGate is not here — it's applied at GUI EDIT time, i.e. when a
 *  button is being created; this is button EXECUTION time, where any player
 *  can click an already pre-approved button.)
 */
public final class GuiButtonExecutor {

    private GuiButtonExecutor() {}

    /**
     * @param player   The player who clicked the button.
     * @param action   The pre-validated action type assigned to this button.
     * @param rawParam The parameter accompanying the action (e.g. page ID, sound ID).
     * @return true if the action was executed, false if a gate rejected it.
     */
    public static boolean execute(ServerPlayerEntity player, ButtonAction action, String rawParam) {
        // GATE 1: Rate limit. If clicks are too rapid, reject silently.
        if (!RateLimitGate.tryAcquire(player.getUuid())) {
            return false;
        }

        // GATE 2: Action + parameter allowlist validation.
        // This is the check that REPLACES the original datapack's
        // "$function $(function_file)" arbitrary code execution.
        if (!ActionAllowlistGate.isAllowed(action, rawParam)) {
            player.sendMessage(
                    net.minecraft.text.Text.literal(
                            "GUI Maker: This action is invalid or not permitted."),
                    false
            );
            return false;
        }

        // Passed both gates -> safe to execute now.
        return dispatch(player, action, rawParam);
    }

    // The actual work happens here, branching over ButtonAction's closed
    // list. Note: there is never "execute rawParam as a command/function"
    // here. rawParam is only ever used as a key pointing to a PREDEFINED record.
    private static boolean dispatch(ServerPlayerEntity player, ButtonAction action, String rawParam) {
        switch (action) {
            case OPEN_PAGE:
                // rawParam here is just a page index — NOT a file path or
                // function name. GuiPageManager looks it up from its own
                // (mod-defined) page list.
                return GuiPageManager.openPage(player, Integer.parseInt(rawParam.trim()));
            case CLOSE_GUI:
                player.closeHandledScreen();
                return true;
            case PLAY_SOUND:
                GuiSoundPlayer.play(player, rawParam);
                return true;
            case GIVE_ITEM:
                GuiItemGiver.give(player, rawParam);
                return true;
            case NO_OP:
            default:
                return true;
        }
    }
}
