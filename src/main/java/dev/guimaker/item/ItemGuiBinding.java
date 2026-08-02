package dev.guimaker.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Locale;
import java.util.Optional;

/** Stores a GUI-opening binding in the vanilla minecraft:custom_data component. */
public final class ItemGuiBinding {
    private static final String ROOT_KEY = "guimaker";
    private static final String BINDING_KEY = "open_gui";

    private ItemGuiBinding() {}

    public enum ClickMode {
        RIGHT,
        LEFT,
        BOTH;

        public static ClickMode parse(String raw) {
            try {
                return valueOf(raw.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException error) {
                throw new IllegalArgumentException("Click mode must be right, left or both.");
            }
        }

        public String commandName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean accepts(ClickType clickType) {
            return this == BOTH
                    || (this == RIGHT && clickType == ClickType.RIGHT)
                    || (this == LEFT && clickType == ClickType.LEFT);
        }
    }

    public enum ClickType {
        RIGHT,
        LEFT
    }

    public record Binding(String guiId, int pageId, ClickMode clickMode) {}

    public static void bind(ItemStack stack, String guiId, int pageId, ClickMode clickMode) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("You must hold an item in your main hand.");
        }
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, root -> {
            NbtCompound guimaker = root.getCompoundOrEmpty(ROOT_KEY);
            NbtCompound binding = new NbtCompound();
            binding.putString("gui", guiId);
            binding.putInt("page", pageId);
            binding.putString("click", clickMode.commandName());
            guimaker.put(BINDING_KEY, binding);
            root.put(ROOT_KEY, guimaker);
        });
    }

    public static boolean unbind(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("You must hold an item in your main hand.");
        }
        if (read(stack).isEmpty()) {
            return false;
        }
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, root -> {
            NbtCompound guimaker = root.getCompoundOrEmpty(ROOT_KEY);
            guimaker.remove(BINDING_KEY);
            if (guimaker.isEmpty()) {
                root.remove(ROOT_KEY);
            } else {
                root.put(ROOT_KEY, guimaker);
            }
        });
        return true;
    }

    public static Optional<Binding> read(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null || component.isEmpty()) {
            return Optional.empty();
        }
        NbtCompound root = component.copyNbt();
        Optional<NbtCompound> guimaker = root.getCompound(ROOT_KEY);
        if (guimaker.isEmpty()) {
            return Optional.empty();
        }
        Optional<NbtCompound> binding = guimaker.get().getCompound(BINDING_KEY);
        if (binding.isEmpty()) {
            return Optional.empty();
        }
        String guiId = binding.get().getString("gui", "");
        int pageId = binding.get().getInt("page", -1);
        String click = binding.get().getString("click", "");
        if (guiId.isBlank() || pageId < 0 || pageId > 9999) {
            return Optional.empty();
        }
        try {
            return Optional.of(new Binding(guiId, pageId, ClickMode.parse(click)));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
