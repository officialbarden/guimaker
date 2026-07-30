package dev.guimaker.gui;

import dev.guimaker.action.ButtonAction;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.gate.ActionAllowlistGate;
import dev.guimaker.gate.RateLimitGate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** The only runtime action dispatcher; raw commands/functions are never run. */
public final class GuiButtonExecutor {
    private GuiButtonExecutor() {}

    public static boolean execute(ServerPlayerEntity player, String guiId,
                                  ButtonAction action, String rawParameter) {
        if (!RateLimitGate.tryAcquire(player.getUuid())) {
            return false;
        }
        return executeAfterRateLimit(player, guiId, action, rawParameter);
    }

    public static boolean executeAfterRateLimit(ServerPlayerEntity player, String guiId,
                                                ButtonAction action, String rawParameter) {
        if (!ActionAllowlistGate.isAllowed(action, rawParameter)) {
            player.sendMessage(Text.literal("GUI Maker: Invalid or disallowed action.")
                    .formatted(Formatting.RED), false);
            return false;
        }

        return switch (action) {
            case OPEN_PAGE -> openPage(player, guiId, rawParameter);
            case CLOSE_GUI -> {
                player.closeHandledScreen();
                yield true;
            }
            case PLAY_SOUND -> {
                GuiSoundPlayer.play(player, rawParameter);
                yield true;
            }
            case GIVE_ITEM -> {
                GuiItemGiver.give(player, rawParameter);
                yield true;
            }
            case RUN_COMMAND -> GatedCommandExecutor.execute(player, rawParameter);
            case NO_OP -> true;
        };
    }

    private static boolean openPage(ServerPlayerEntity player, String guiId, String parameter) {
        int pageId = Integer.parseInt(parameter.strip());
        if (!GuiRepository.requireGui(guiId).hasPage(pageId)) {
            player.sendMessage(Text.literal("GUI Maker: The target page no longer exists.")
                    .formatted(Formatting.RED), false);
            return false;
        }
        return GuiPageManager.openGui(player, guiId, pageId);
    }
}
