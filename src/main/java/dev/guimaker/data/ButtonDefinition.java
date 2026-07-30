package dev.guimaker.data;

import dev.guimaker.action.ButtonAction;
import dev.guimaker.widget.WidgetDefinition;
import net.minecraft.item.ItemStack;

/** A visible GUI item with an optional safe action and stateful widget. */
public final class ButtonDefinition {
    private ItemStack displayStack;
    private ButtonAction action;
    private String parameter;
    private WidgetDefinition widget;

    public ButtonDefinition(ItemStack displayStack, ButtonAction action, String parameter) {
        this(displayStack, action, parameter, null);
    }

    public ButtonDefinition(ItemStack displayStack, ButtonAction action, String parameter,
                            WidgetDefinition widget) {
        this.displayStack = safeCopy(displayStack);
        this.action = action == null ? ButtonAction.NO_OP : action;
        this.parameter = parameter == null ? "" : parameter;
        this.widget = widget;
    }

    public static ButtonDefinition empty() {
        return new ButtonDefinition(ItemStack.EMPTY, ButtonAction.NO_OP, "", null);
    }

    public ItemStack displayStack() { return safeCopy(displayStack); }
    public void setDisplayStack(ItemStack stack) { this.displayStack = safeCopy(stack); }
    public ButtonAction action() { return action; }
    public String parameter() { return parameter; }
    public WidgetDefinition widget() { return widget; }
    public void setWidget(WidgetDefinition widget) { this.widget = widget; }

    public void setAction(ButtonAction action, String parameter) {
        this.action = action == null ? ButtonAction.NO_OP : action;
        this.parameter = parameter == null ? "" : parameter;
    }

    public boolean isCompletelyEmpty() {
        return displayStack.isEmpty() && action == ButtonAction.NO_OP && widget == null;
    }

    private static ItemStack safeCopy(ItemStack stack) {
        return stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }
}
