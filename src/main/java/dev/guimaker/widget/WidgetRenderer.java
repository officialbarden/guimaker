package dev.guimaker.widget;

import dev.guimaker.data.ButtonDefinition;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Creates the current visual stack for a stateful chest-GUI widget. */
public final class WidgetRenderer {
    private WidgetRenderer() {}

    public static ItemStack render(ButtonDefinition button, ServerPlayerEntity player) {
        WidgetDefinition widget = button.widget();
        if (widget == null) return button.displayStack();
        String scope = widget.scope() == WidgetDefinition.Scope.PLAYER ? "PLAYER" : "WORLD";
        return switch (widget.type()) {
            case TOGGLE -> {
                boolean enabled = WidgetStateRepository.getToggle(widget, player);
                ItemStack stack = new ItemStack(enabled ? Items.LIME_DYE : Items.RED_DYE);
                stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(widget.key() + ": " + (enabled ? "ON" : "OFF"))
                        .formatted(enabled ? Formatting.GREEN : Formatting.RED));
                yield stack;
            }
            case CYCLE -> {
                String value = WidgetStateRepository.getCycle(widget, player);
                ItemStack stack = new ItemStack(Items.CLOCK);
                stack.set(DataComponentTypes.CUSTOM_NAME,
                        Text.literal(widget.key() + ": " + value + " [" + scope + "]").formatted(Formatting.AQUA));
                yield stack;
            }
            case COUNTER -> {
                int value = WidgetStateRepository.getCounter(widget, player);
                ItemStack stack = new ItemStack(Items.COMPARATOR);
                stack.set(DataComponentTypes.CUSTOM_NAME,
                        Text.literal(widget.key() + ": " + value + " [L+ / R-]").formatted(Formatting.YELLOW));
                yield stack;
            }
            case ITEM_HOLDER -> WidgetStateRepository.getHolder(widget, player);
        };
    }
}
