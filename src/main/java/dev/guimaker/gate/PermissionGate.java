package dev.guimaker.gate;

import net.minecraft.server.command.ServerCommandSource;

/**
 * PermissionGate — GATE #1: Permission check.
 *
 * WHY IT WAS MISSING IN THE ORIGINAL DATAPACK:
 * The audit found no "op_level", "permission", or "hasPermission" checks
 * anywhere. This meant ANY player on the server (even non-ops) could open
 * the GUI editor and configure button behavior.
 *
 * WHAT THIS CLASS DOES:
 * This gate is called before GUI editing (create/edit/delete) commands.
 * If the player's permission level is insufficient, the operation is
 * rejected BEFORE it starts. In vanilla Minecraft, op levels range 0-4;
 * we require level 2 (normal op / "gamemaster") here.
 */
public final class PermissionGate {

    // Minimum required permission level. 2 = normal operator (vanilla default).
    private static final int REQUIRED_EDIT_LEVEL = 2;

    // Prevents instantiation (this class only holds static methods).
    private PermissionGate() {}

    /**
     * Called before GUI editing operations (create/edit/delete).
     * source: represents whoever issued the command (player, console, etc).
     * Returns: true if the operation may proceed, false if it must be denied.
     */
    public static boolean canEditGui(ServerCommandSource source) {
        // hasPermissionLevel: uses Minecraft's own permission system.
        // This is a check the original datapack never provided natively.
        return source.hasPermissionLevel(REQUIRED_EDIT_LEVEL);
    }

    /**
     * Standard message shown to the player when an unauthorized attempt occurs.
     * The reason for rejection is stated explicitly (no silent failure).
     */
    public static String denialMessage() {
        return "GUI Maker: You do not have permission to do this (required level: "
                + REQUIRED_EDIT_LEVEL + ").";
    }
}
