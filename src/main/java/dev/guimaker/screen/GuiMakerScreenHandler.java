package dev.guimaker.screen;

import dev.guimaker.action.ButtonAction;
import dev.guimaker.data.ButtonDefinition;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.data.PageDefinition;
import dev.guimaker.gate.RateLimitGate;
import dev.guimaker.gui.GuiButtonExecutor;
import dev.guimaker.widget.WidgetDefinition;
import dev.guimaker.widget.WidgetRenderer;
import dev.guimaker.widget.WidgetStateRepository;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Runtime chest GUI with safe buttons and stateful widget slots. */
public final class GuiMakerScreenHandler extends GenericContainerScreenHandler {
    private final String guiId;
    private final int pageId;
    private final int guiSlotCount;
    private final Set<Integer> holderSlots = new HashSet<>();
    private final Set<Integer> unavailableHolderSlots = new HashSet<>();
    private final Map<Integer, WidgetDefinition> ownedHolderWidgets = new java.util.HashMap<>();

    public GuiMakerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                 int rows, String guiId, int pageId) {
        super(typeForRows(rows), syncId, playerInventory, inventory, rows);
        this.guiId = guiId;
        this.pageId = pageId;
        this.guiSlotCount = rows * 9;

        if (playerInventory.player instanceof ServerPlayerEntity player) {
            try {
                PageDefinition page = GuiRepository.requirePage(guiId, pageId);
                for (Map.Entry<Integer, ButtonDefinition> entry : page.buttons().entrySet()) {
                    WidgetDefinition widget = entry.getValue().widget();
                    if (widget != null && widget.type() == WidgetDefinition.Type.ITEM_HOLDER) {
                        int slot = entry.getKey();
                        if (WidgetStateRepository.acquireHolder(widget, player)) {
                            holderSlots.add(slot);
                            ownedHolderWidgets.put(slot, widget);
                        } else {
                            unavailableHolderSlots.add(slot);
                            inventory.setStack(slot, holderBusyStack());
                        }
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // The page was removed while opening; subsequent clicks close it.
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int mouseButton, SlotActionType actionType, PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        PageDefinition page;
        try {
            page = GuiRepository.requirePage(guiId, pageId);
        } catch (IllegalArgumentException ignored) {
            serverPlayer.closeHandledScreen();
            return;
        }

        // Player inventory remains usable only when this screen owns at least
        // one item-holder lock. Shift-move is still blocked by quickMove().
        if (slotIndex >= guiSlotCount) {
            if (!holderSlots.isEmpty() && actionType == SlotActionType.PICKUP) {
                super.onSlotClick(slotIndex, mouseButton, actionType, player);
            }
            return;
        }
        if (slotIndex < 0 || unavailableHolderSlots.contains(slotIndex)) return;

        ButtonDefinition definition = page.button(slotIndex);
        if (definition == null) return;
        WidgetDefinition widget = definition.widget();

        if (widget != null && widget.type() == WidgetDefinition.Type.ITEM_HOLDER) {
            if (holderSlots.contains(slotIndex) && actionType == SlotActionType.PICKUP) {
                super.onSlotClick(slotIndex, mouseButton, actionType, player);
                WidgetStateRepository.setHolder(widget, serverPlayer,
                        getInventory().getStack(slotIndex));
                sendContentUpdates();
            }
            return;
        }

        if (widget != null) {
            if (actionType != SlotActionType.PICKUP || !RateLimitGate.tryAcquire(serverPlayer.getUuid())) return;
            switch (widget.type()) {
                case TOGGLE -> WidgetStateRepository.toggle(widget, serverPlayer);
                case CYCLE -> WidgetStateRepository.cycle(widget, serverPlayer);
                case COUNTER -> WidgetStateRepository.changeCounter(widget, serverPlayer,
                        mouseButton == 1 ? -1 : 1);
                case ITEM_HOLDER -> { }
            }
            getInventory().setStack(slotIndex, WidgetRenderer.render(definition, serverPlayer));
            sendContentUpdates();
            if (definition.action() != ButtonAction.NO_OP) {
                // The widget already acquired the rate gate, so dispatch its
                // optional action without acquiring it a second time.
                GuiButtonExecutor.executeAfterRateLimit(serverPlayer, guiId,
                        definition.action(), definition.parameter());
            }
            return;
        }

        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE) return;
        if (definition.action() != ButtonAction.NO_OP) {
            GuiButtonExecutor.execute(serverPlayer, guiId, definition.action(), definition.parameter());
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ownedHolderWidgets.forEach((slot, widget) -> {
                WidgetStateRepository.setHolder(widget, serverPlayer, getInventory().getStack(slot));
                WidgetStateRepository.releaseHolder(widget, serverPlayer);
            });
        }
        ownedHolderWidgets.clear();
        holderSlots.clear();
        super.onClosed(player);
    }

    private static ItemStack holderBusyStack() {
        ItemStack stack = new ItemStack(Items.BARRIER);
        stack.set(DataComponentTypes.CUSTOM_NAME,
                Text.literal("Item holder is currently in use").formatted(Formatting.RED));
        return stack;
    }

    public static ScreenHandlerType<GenericContainerScreenHandler> typeForRows(int rows) {
        return switch (rows) {
            case 1 -> ScreenHandlerType.GENERIC_9X1;
            case 2 -> ScreenHandlerType.GENERIC_9X2;
            case 3 -> ScreenHandlerType.GENERIC_9X3;
            case 4 -> ScreenHandlerType.GENERIC_9X4;
            case 5 -> ScreenHandlerType.GENERIC_9X5;
            case 6 -> ScreenHandlerType.GENERIC_9X6;
            default -> throw new IllegalArgumentException("The row count must be between 1 and 6.");
        };
    }

    public static Inventory blankInventory(int rows) {
        return new SimpleInventory(rows * 9);
    }
}
