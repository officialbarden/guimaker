package com.runtoolkit.guimaker.gui;

import com.mojang.brigadier.CommandDispatcher;
import com.runtoolkit.guimaker.gate.PermissionGate;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * GuiEditCommand — Registration point for the "/guimaker edit" command.
 *
 * PermissionGate IS CALLED HERE: in the original datapack, GUI editing was
 * open to any player (no op check existed). Here, before the command runs,
 * requires(...) checks PermissionGate.canEditGui. Brigadier (Minecraft's
 * command library) won't even SHOW the command to a player if requires()
 * returns false (it won't appear in tab-completion) — this ensures
 * "unauthorized players never even see the command" rather than
 * "unauthorized players see it, try it, then get rejected" (a safer pattern).
 */
public final class GuiEditCommand {

    private GuiEditCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("guimaker")
                        .then(CommandManager.literal("edit")
                                // requires: PermissionGate is enforced here.
                                .requires(PermissionGate::canEditGui)
                                .executes(context -> {
                                    // Gate already passed (thanks to requires()).
                                    // Actual editor-opening logic will be added here later.
                                    context.getSource().sendFeedback(
                                            () -> net.minecraft.text.Text.literal(
                                                    "Opening GUI Maker editor..."),
                                            false
                                    );
                                    return 1;
                                })
                        )
        );
    }
}
