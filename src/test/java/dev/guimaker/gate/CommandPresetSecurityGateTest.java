package dev.guimaker.gate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandPresetSecurityGateTest {
    @Test
    void serverLevelThreeRequiresExplicitAcknowledgement() {
        var preset = preset("say safe", 3, false);
        assertTrue(CommandPresetRegistry.requiresSecurityAcknowledgement(preset));
        assertFalse(preset.securityAcknowledged());
    }

    @Test
    void nestedSensitiveCommandRequiresExplicitAcknowledgement() {
        var preset = preset("execute as @s run minecraft:op Example", 2, false);
        assertTrue(CommandPresetRegistry.requiresSecurityAcknowledgement(preset));
    }

    @Test
    void normalLevelTwoCommandDoesNotRequireAcknowledgement() {
        assertFalse(CommandPresetRegistry.requiresSecurityAcknowledgement(
                preset("tellraw @s {\"text\":\"ok\"}", 2, false)));
    }

    @Test
    void acknowledgedPresetRemainsMarkedForGateButCanBeApprovedByExecutionGate() {
        var preset = preset("stop", 4, true);
        assertTrue(CommandPresetRegistry.requiresSecurityAcknowledgement(preset));
        assertTrue(preset.securityAcknowledged());
    }

    private static CommandPresetRegistry.CommandPreset preset(String command, int level,
                                                               boolean acknowledged) {
        return new CommandPresetRegistry.CommandPreset(
                "test", command, CommandPresetRegistry.ExecutionMode.SERVER,
                level, 0, 250L, true, acknowledged);
    }
}
