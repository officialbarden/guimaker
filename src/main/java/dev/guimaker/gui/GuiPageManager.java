package dev.guimaker.gui;

import dev.guimaker.data.GuiDefinition;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.data.PageDefinition;
import dev.guimaker.screen.GuiEditorScreenHandler;
import dev.guimaker.screen.GuiMakerScreenHandler;
import dev.guimaker.util.PlaceholderResolver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** Opens runtime and editor pages using vanilla chest screens. */
public final class GuiPageManager {
    private GuiPageManager() {}

    public static boolean openGui(ServerPlayerEntity player, String guiId, int pageId) {
        final GuiDefinition gui;
        final PageDefinition page;
        try {
            gui = GuiRepository.requireGui(guiId);
            page = GuiRepository.requirePage(gui.id(), pageId);
        } catch (IllegalArgumentException error) {
            return false;
        }

        Inventory inventory = page.createRuntimeInventory(player);
        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal(PlaceholderResolver.resolve(page.title(), player));
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new GuiMakerScreenHandler(syncId, playerInventory, inventory,
                        page.rows(), gui.id(), page.id());
            }
        });
        return true;
    }

    public static boolean openEditor(ServerPlayerEntity player, String guiId, int pageId) {
        final GuiDefinition gui;
        final PageDefinition page;
        try {
            gui = GuiRepository.requireGui(guiId);
            page = GuiRepository.requirePage(gui.id(), pageId);
        } catch (IllegalArgumentException error) {
            return false;
        }

        Inventory inventory = page.createInventory();
        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal("Edit: " + gui.id() + " #" + page.id());
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new GuiEditorScreenHandler(syncId, playerInventory, inventory,
                        page.rows(), gui.id(), page.id());
            }
        });
        return true;
    }

    /** Compatibility helper: opens the requested page in the first GUI. */
    public static boolean openPage(ServerPlayerEntity player, int pageId) {
        return GuiRepository.ids().stream().findFirst()
                .map(id -> openGui(player, id, pageId))
                .orElse(false);
    }
}
