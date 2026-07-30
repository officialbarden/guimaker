package com.runtoolkit.guimaker.gui;

import com.runtoolkit.guimaker.screen.GuiMakerScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * GuiPageManager — Manages page transitions.
 *
 * NOTE: openPage only takes an integer index; this index is NEVER converted
 * into a file path, a function name, or any executable string. Page content
 * is read from PageRegistry (fixed, in-mod data); a player cannot inject new
 * executable content at runtime.
 */
public final class GuiPageManager {

    private GuiPageManager() {}

    public static boolean openPage(ServerPlayerEntity player, int pageIndex) {
        if (pageIndex < 0 || !PageRegistry.exists(pageIndex)) {
            // If an unregistered page is requested, reject; never
            // "try an unknown path anyway."
            return false;
        }

        PageRegistry.PageData data = PageRegistry.get(pageIndex);
        Inventory inventory = GuiMakerScreenHandler.blankInventory(data.rows());

        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal(data.title());
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new GuiMakerScreenHandler(syncId, playerInventory, inventory, data.rows());
            }
        });

        return true;
    }
}
