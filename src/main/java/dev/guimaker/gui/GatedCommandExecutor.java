package dev.guimaker.gui;

import dev.guimaker.gate.CommandExecutionGate;
import dev.guimaker.util.PlaceholderResolver;
import dev.guimaker.gate.CommandPresetRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/** Executes only server-owned, allowlisted command presets after all gates pass. */
public final class GatedCommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger("guimaker");

    private GatedCommandExecutor() {}

    public static boolean execute(ServerPlayerEntity player, String presetId) {
        return execute(player, presetId, Map.of());
    }

    public static boolean execute(ServerPlayerEntity player, String presetId,
                                  Map<String, String> keyValues) {
        CommandExecutionGate.Decision decision = CommandExecutionGate.tryAcquire(player, presetId);
        switch (decision) {
            case PRESET_NOT_FOUND -> {
                player.sendMessage(Text.literal("GUI Maker: Command preset not found.")
                        .formatted(Formatting.RED), false);
                return false;
            }
            case INSUFFICIENT_PERMISSION -> {
                player.sendMessage(Text.literal("GUI Maker: You do not have permission to run this command preset.")
                        .formatted(Formatting.RED), false);
                return false;
            }
            case SECURITY_ACK_REQUIRED -> {
                player.sendMessage(Text.literal("GUI Maker: This high-risk command preset requires explicit server-owner acknowledgement.")
                        .formatted(Formatting.RED), false);
                return false;
            }
            case COOLDOWN -> {
                player.sendMessage(Text.literal("GUI Maker: This command preset is cooling down.")
                        .formatted(Formatting.YELLOW), true);
                return false;
            }
            case ALLOWED -> {
                // Continue below.
            }
        }

        CommandPresetRegistry.CommandPreset preset = CommandPresetRegistry.get(presetId);
        if (preset == null) {
            return false;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        String command = PlaceholderResolver.resolveCommand(preset.command(), player, keyValues);

        ServerCommandSource source;
        if (preset.runAs() == CommandPresetRegistry.ExecutionMode.PLAYER) {
            // Never elevate a player's permission level.
            source = player.getCommandSource();
        } else {
            // Server presets run at the explicitly configured permission level,
            // in the clicking player's world and position.
            source = server.getCommandSource()
                    .withWorld(player.getWorld())
                    .withPosition(player.getPos())
                    .withRotation(player.getRotationClient())
                    .withLevel(preset.serverPermissionLevel());
        }
        if (preset.silent()) {
            source = source.withSilent();
        }

        try {
            server.getCommandManager().executeWithPrefix(source, command);
            LOGGER.info("Executed GUI command preset '{}' for player {} ({}) as {}.",
                    preset.id(), player.getGameProfile().getName(), player.getUuidAsString(), preset.runAs());
            return true;
        } catch (Exception error) {
            LOGGER.error("Command preset '{}' failed for player {} ({}).",
                    preset.id(), player.getGameProfile().getName(), player.getUuidAsString(), error);
            player.sendMessage(Text.literal("GUI Maker: The command preset failed.")
                    .formatted(Formatting.RED), false);
            return false;
        }
    }
}
