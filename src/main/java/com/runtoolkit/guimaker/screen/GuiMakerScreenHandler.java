package com.runtoolkit.guimaker.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

/**
 * GuiMakerScreenHandler — Gercek envanter ekranini sunucu tarafinda temsil eder.
 *
 * Vanilla GenericContainerScreenHandler'i genisletir (chest GUI benzeri).
 * Icerik (Inventory), PageRegistry'den gelen SABIT veriyle doldurulur;
 * oyuncunun tikladigi slotlar GuiButtonExecutor uzerinden, sadece
 * ButtonAction enum degerleriyle islenir.
 */
public final class GuiMakerScreenHandler extends GenericContainerScreenHandler {

    public GuiMakerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(pickType(rows), syncId, playerInventory, inventory, rows);
    }

    private static ScreenHandlerType<GenericContainerScreenHandler> pickType(int rows) {
        return switch (rows) {
            case 1 -> ScreenHandlerType.GENERIC_9X1;
            case 2 -> ScreenHandlerType.GENERIC_9X2;
            case 3 -> ScreenHandlerType.GENERIC_9X3;
            case 4 -> ScreenHandlerType.GENERIC_9X4;
            case 5 -> ScreenHandlerType.GENERIC_9X5;
            default -> ScreenHandlerType.GENERIC_9X6;
        };
    }

    public static Inventory blankInventory(int rows) {
        return new SimpleInventory(rows * 9);
    }
}
