package com.runtoolkit.guimaker.screen;

import com.runtoolkit.guimaker.data.ButtonDefinition;
import com.runtoolkit.guimaker.data.GuiRepository;
import com.runtoolkit.guimaker.data.PageDefinition;
import com.runtoolkit.guimaker.gui.GuiButtonExecutor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

/** Read-only chest GUI. Display items can never be taken or moved. */
public final class GuiMakerScreenHandler extends GenericContainerScreenHandler {
    private final String guiId;
    private final int pageId;
    private final int guiSlotCount;

    public GuiMakerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                 int rows, String guiId, int pageId) {
        super(typeForRows(rows), syncId, playerInventory, inventory, rows);
        this.guiId = guiId;
        this.pageId = pageId;
        this.guiSlotCount = rows * 9;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // Never invoke vanilla slot movement: GUI display items are virtual and
        // the player's inventory must not be modified while this menu is open.
        if (slotIndex < 0 || slotIndex >= guiSlotCount || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE) {
            return;
        }

        PageDefinition page;
        try {
            page = GuiRepository.requirePage(guiId, pageId);
        } catch (IllegalArgumentException ignored) {
            serverPlayer.closeHandledScreen();
            return;
        }
        ButtonDefinition definition = page.button(slotIndex);
        if (definition == null || definition.action() == com.runtoolkit.guimaker.action.ButtonAction.NO_OP) {
            return;
        }
        GuiButtonExecutor.execute(serverPlayer, guiId, definition.action(), definition.parameter());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    public static ScreenHandlerType<GenericContainerScreenHandler> typeForRows(int rows) {
        return switch (rows) {
            case 1 -> ScreenHandlerType.GENERIC_9X1;
            case 2 -> ScreenHandlerType.GENERIC_9X2;
            case 3 -> ScreenHandlerType.GENERIC_9X3;
            case 4 -> ScreenHandlerType.GENERIC_9X4;
            case 5 -> ScreenHandlerType.GENERIC_9X5;
            case 6 -> ScreenHandlerType.GENERIC_9X6;
            default -> throw new IllegalArgumentException("Satır sayısı 1-6 olmalıdır.");
        };
    }

    public static Inventory blankInventory(int rows) {
        return new SimpleInventory(rows * 9);
    }
}
