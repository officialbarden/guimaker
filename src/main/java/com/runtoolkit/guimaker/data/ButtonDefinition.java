package com.runtoolkit.guimaker.data;

import com.runtoolkit.guimaker.action.ButtonAction;
import net.minecraft.item.ItemStack;

/** A visible GUI item and its safe, allowlisted action. */
public final class ButtonDefinition {
    private ItemStack displayStack;
    private ButtonAction action;
    private String parameter;

    public ButtonDefinition(ItemStack displayStack, ButtonAction action, String parameter) {
        this.displayStack = safeCopy(displayStack);
        this.action = action == null ? ButtonAction.NO_OP : action;
        this.parameter = parameter == null ? "" : parameter;
    }

    public static ButtonDefinition empty() {
        return new ButtonDefinition(ItemStack.EMPTY, ButtonAction.NO_OP, "");
    }

    public ItemStack displayStack() {
        return safeCopy(displayStack);
    }

    public void setDisplayStack(ItemStack stack) {
        this.displayStack = safeCopy(stack);
    }

    public ButtonAction action() {
        return action;
    }

    public String parameter() {
        return parameter;
    }

    public void setAction(ButtonAction action, String parameter) {
        this.action = action == null ? ButtonAction.NO_OP : action;
        this.parameter = parameter == null ? "" : parameter;
    }

    public boolean isCompletelyEmpty() {
        return displayStack.isEmpty() && action == ButtonAction.NO_OP;
    }

    private static ItemStack safeCopy(ItemStack stack) {
        return stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }
}
