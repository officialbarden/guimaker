package com.runtoolkit.guimaker;

import com.runtoolkit.guimaker.gate.RateLimitGate;
import com.runtoolkit.guimaker.gui.GuiEditCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * GuiMakerMod — The "entrypoint" class specified in fabric.mod.json.
 * onInitialize() is called ONCE when the server starts (per the
 * ModInitializer interface). Commands and event listeners are registered here.
 */
public final class GuiMakerMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Register commands (/guimaker edit, etc.) — PermissionGate is enforced here.
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> GuiEditCommand.register(dispatcher)
        );

        // Clear a player's RateLimitGate record on disconnect (to prevent a
        // memory leak — per the TPS/performance rule).
        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> RateLimitGate.clear(handler.getPlayer().getUuid())
        );
    }
}
