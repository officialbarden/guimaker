package com.runtoolkit.guimaker;

import com.runtoolkit.guimaker.data.GuiRepository;
import com.runtoolkit.guimaker.gate.CommandExecutionGate;
import com.runtoolkit.guimaker.gate.RateLimitGate;
import com.runtoolkit.guimaker.gui.GuiEditCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** GUI Maker Fabric entry point. */
public final class GuiMakerMod implements ModInitializer {
    public static final String MOD_ID = "guimaker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> GuiEditCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTED.register(GuiRepository::load);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> GuiRepository.close());

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            RateLimitGate.clear(handler.getPlayer().getUuid());
            CommandExecutionGate.clearPlayer(handler.getPlayer().getUuid());
        });

        LOGGER.info("GUI Maker initialized.");
    }
}
