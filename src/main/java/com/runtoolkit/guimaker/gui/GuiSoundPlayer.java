package com.runtoolkit.guimaker.gui;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

/**
 * GuiSoundPlayer — Performs the sound-playing operation.
 *
 * The 'soundId' arriving here has already been validated by
 * ActionAllowlistGate as being registered in SoundRegistry. Still, as a
 * second defense layer (defense in depth), any errors that could occur
 * while constructing the Identifier are caught here.
 */
public final class GuiSoundPlayer {

    private GuiSoundPlayer() {}

    public static void play(ServerPlayerEntity player, String soundId) {
        try {
            Identifier id = Identifier.of(soundId);
            SoundEvent event = SoundEvent.of(id);
            player.playSoundToPlayer(event, SoundCategory.MASTER, 1.0f, 1.0f);
        } catch (Exception e) {
            // If an unexpected formatting error occurs, swallow it silently —
            // never crash the server.
        }
    }
}
