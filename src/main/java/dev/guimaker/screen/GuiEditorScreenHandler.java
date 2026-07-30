package dev.guimaker.screen;

import dev.guimaker.data.ButtonDefinition;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.data.PageDefinition;
import dev.guimaker.gate.PermissionGate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Vanilla-client-compatible visual editor.
 * Left click copies the operator's main-hand item; right click clears the slot.
 * Items are copied and are never consumed or returned, preventing duplication.
 */
public final class GuiEditorScreenHandler extends GenericContainerScreenHandler {
    private final String guiId;
    private final int pageId;
    private final int guiSlotCount;
    private final Inventory editorInventory;

    public GuiEditorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                  int rows, String guiId, int pageId) {
        super(GuiMakerScreenHandler.typeForRows(rows), syncId, playerInventory, inventory, rows);
        this.guiId = guiId;
        this.pageId = pageId;
        this.guiSlotCount = rows * 9;
        this.editorInventory = inventory;
    }

    @Override
    public void onSlotClick(int slotIndex, int mouseButton, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex < 0 || slotIndex >= guiSlotCount || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        if (!PermissionGate.canEditGui(serverPlayer.getCommandSource())) {
            serverPlayer.closeHandledScreen();
            return;
        }
        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE) {
            return;
        }

        try {
            if (mouseButton == 1 || actionType == SlotActionType.QUICK_MOVE) {
                GuiRepository.clearButton(guiId, pageId, slotIndex);
                editorInventory.setStack(slotIndex, ItemStack.EMPTY);
                sendContentUpdates();
                serverPlayer.sendMessage(Text.literal("Slot " + slotIndex + " cleared.")
                        .formatted(Formatting.YELLOW), true);
                return;
            }

            ItemStack held = serverPlayer.getMainHandStack();
            if (held.isEmpty()) {
                ButtonDefinition current = GuiRepository.requirePage(guiId, pageId).button(slotIndex);
                String action = current == null ? "NO_OP" : current.action().name()
                        + (current.parameter().isEmpty() ? "" : " (" + current.parameter() + ")");
                serverPlayer.sendMessage(Text.literal("Your main hand is empty. Slot " + slotIndex
                        + " action: " + action).formatted(Formatting.GRAY), true);
                return;
            }

            GuiRepository.setItem(guiId, pageId, slotIndex, held);
            editorInventory.setStack(slotIndex, held.copy());
            sendContentUpdates();
            serverPlayer.sendMessage(Text.literal("Slot " + slotIndex
                    + " was updated with the item in your main hand.").formatted(Formatting.GREEN), true);
        } catch (IllegalArgumentException error) {
            serverPlayer.sendMessage(Text.literal("GUI Maker: " + error.getMessage())
                    .formatted(Formatting.RED), true);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
}
