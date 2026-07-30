package dev.guimaker.gui;

import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/** Plays only sound IDs that already passed SoundRegistry validation. */
public final class GuiSoundPlayer {
    private GuiSoundPlayer() {}

    public static void play(ServerPlayerEntity player, String soundId) {
        try {
            SoundEvent event = Registries.SOUND_EVENT.get(Identifier.of(soundId));
            if (event != null) {
                player.playSoundToPlayer(event, SoundCategory.MASTER, 1.0f, 1.0f);
            }
        } catch (Exception ignored) {
            // A malformed or missing registry value must never crash the server.
        }
    }
}
