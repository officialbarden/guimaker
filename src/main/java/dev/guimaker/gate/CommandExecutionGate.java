package dev.guimaker.gate;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Permission and per-preset cooldown gate for command execution. */
public final class CommandExecutionGate {
    private static final ConcurrentHashMap<CooldownKey, Long> LAST_EXECUTION = new ConcurrentHashMap<>();

    private CommandExecutionGate() {}

    public enum Decision {
        ALLOWED,
        PRESET_NOT_FOUND,
        INSUFFICIENT_PERMISSION,
        SECURITY_ACK_REQUIRED,
        COOLDOWN
    }

    public static Decision tryAcquire(ServerPlayerEntity player, String presetId) {
        CommandPresetRegistry.CommandPreset preset = CommandPresetRegistry.get(presetId);
        if (preset == null) {
            return Decision.PRESET_NOT_FOUND;
        }
        if (!player.getCommandSource().hasPermissionLevel(preset.requiredPlayerPermissionLevel())) {
            return Decision.INSUFFICIENT_PERMISSION;
        }
        if (CommandPresetRegistry.requiresSecurityAcknowledgement(preset)
                && !preset.securityAcknowledged()) {
            return Decision.SECURITY_ACK_REQUIRED;
        }

        CooldownKey key = new CooldownKey(player.getUuid(), preset.id());
        long now = System.currentTimeMillis();
        boolean[] acquired = {false};
        LAST_EXECUTION.compute(key, (ignored, previous) -> {
            if (previous == null || now - previous >= preset.cooldownMs()) {
                acquired[0] = true;
                return now;
            }
            return previous;
        });
        return acquired[0] ? Decision.ALLOWED : Decision.COOLDOWN;
    }

    public static void clearPlayer(UUID playerId) {
        LAST_EXECUTION.keySet().removeIf(key -> key.playerId().equals(playerId));
    }

    public static void clearAll() {
        LAST_EXECUTION.clear();
    }

    private record CooldownKey(UUID playerId, String presetId) {}
}
