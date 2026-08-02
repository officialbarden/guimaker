package dev.guimaker.gui;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * GuiItemGiver — Gives a FIXED, predefined item based on a preset ID.
 *
 * IMPORTANT: presetId is NOT a free NBT/command string like the original
 * datapack's "item_modifier". It's only a keyword; the actual ItemStack is
 * constructed here, in Java code, as a fixed value. No data typed by the
 * player ever feeds directly into ItemStack construction.
 */
public final class GuiItemGiver {

    private GuiItemGiver() {}

    public static void give(ServerPlayerEntity player, String presetId) {
        ItemStack stack = switch (presetId) {
            case "preset.empty_slot_filler" -> new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
            case "preset.back_button_item" -> new ItemStack(Items.ARROW);
            case "preset.confirm_item" -> new ItemStack(Items.LIME_DYE);
            case "preset.cancel_item" -> new ItemStack(Items.RED_DYE);
            default -> ItemStack.EMPTY;
        };

        if (!stack.isEmpty()) {
            player.getInventory().offerOrDrop(stack);
        }
    }
}
