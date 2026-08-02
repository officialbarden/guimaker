package dev.guimaker.data;

import dev.guimaker.action.ButtonAction;
import dev.guimaker.widget.WidgetDefinition;
import dev.guimaker.widget.WidgetRenderer;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** One chest-style page containing between one and six rows. */
public final class PageDefinition {
    private final int id;
    private String title;
    private int rows;
    private final TreeMap<Integer, ButtonDefinition> buttons = new TreeMap<>();

    public PageDefinition(int id, String title, int rows) {
        if (id < 0 || id > 9999) {
            throw new IllegalArgumentException("The page ID must be between 0 and 9999.");
        }
        validateRows(rows);
        this.id = id;
        this.title = sanitizeTitle(title);
        this.rows = rows;
    }

    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = sanitizeTitle(title);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public Map<Integer, ButtonDefinition> buttons() {
        return Collections.unmodifiableMap(buttons);
    }

    public ButtonDefinition button(int slot) {
        validateSlot(slot);
        return buttons.get(slot);
    }

    public void putButton(int slot, ButtonDefinition button) {
        validateSlot(slot);
        if (button == null || button.isCompletelyEmpty()) {
            buttons.remove(slot);
        } else {
            buttons.put(slot, button);
        }
    }

    public void setItem(int slot, ItemStack stack) {
        validateSlot(slot);
        ButtonDefinition button = buttons.computeIfAbsent(slot, ignored -> ButtonDefinition.empty());
        button.setDisplayStack(stack);
        cleanup(slot, button);
    }

    public void setAction(int slot, ButtonAction action, String parameter) {
        validateSlot(slot);
        ButtonDefinition button = buttons.computeIfAbsent(slot, ignored -> ButtonDefinition.empty());
        button.setAction(action, parameter);
        cleanup(slot, button);
    }

    public void setWidget(int slot, WidgetDefinition widget) {
        validateSlot(slot);
        ButtonDefinition button = buttons.computeIfAbsent(slot, ignored -> ButtonDefinition.empty());
        button.setWidget(widget);
        cleanup(slot, button);
    }

    public void clearWidget(int slot) {
        validateSlot(slot);
        ButtonDefinition button = buttons.get(slot);
        if (button != null) {
            button.setWidget(null);
            cleanup(slot, button);
        }
    }

    public void clearButton(int slot) {
        validateSlot(slot);
        buttons.remove(slot);
    }

    public SimpleInventory createInventory() {
        SimpleInventory inventory = new SimpleInventory(size());
        for (Map.Entry<Integer, ButtonDefinition> entry : buttons.entrySet()) {
            inventory.setStack(entry.getKey(), entry.getValue().displayStack());
        }
        return inventory;
    }

    public SimpleInventory createRuntimeInventory(ServerPlayerEntity player) {
        SimpleInventory inventory = new SimpleInventory(size());
        for (Map.Entry<Integer, ButtonDefinition> entry : buttons.entrySet()) {
            inventory.setStack(entry.getKey(), WidgetRenderer.render(entry.getValue(), player));
        }
        return inventory;
    }

    private void cleanup(int slot, ButtonDefinition button) {
        if (button.isCompletelyEmpty()) {
            buttons.remove(slot);
        }
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= size()) {
            throw new IllegalArgumentException("The slot is invalid for this page (0-" + (size() - 1) + ").");
        }
    }

    public static void validateRows(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("The row count must be between 1 and 6.");
        }
    }

    public static String sanitizeTitle(String title) {
        String value = title == null ? "GUI" : title.strip();
        if (value.isEmpty()) {
            value = "GUI";
        }
        value = value.replaceAll("\\p{Cntrl}", "");
        if (value.length() > 64) {
            value = value.substring(0, 64);
        }
        return value;
    }
}
