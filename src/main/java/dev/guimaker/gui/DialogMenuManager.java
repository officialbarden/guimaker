package dev.guimaker.gui;

import com.mojang.serialization.JsonOps;
import com.google.gson.JsonPrimitive;
import dev.guimaker.action.ButtonAction;
import dev.guimaker.data.ButtonDefinition;
import dev.guimaker.data.GuiDefinition;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.data.PageDefinition;
import dev.guimaker.item.ItemGuiBinding;
import dev.guimaker.widget.WidgetDefinition;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.action.DynamicRunCommandDialogAction;
import net.minecraft.dialog.action.ParsedTemplate;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.input.SingleOptionInputControl;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Builds the operator workflow with Minecraft 1.21.8 vanilla dialogs. */
public final class DialogMenuManager {
    private static final int ENTRIES_PER_DIALOG = 10;
    private static final int BUTTON_WIDTH = 200;

    private DialogMenuManager() {}

    public enum MenuMode {
        EDIT,
        OPEN,
        ADD_PAGE,
        BIND_ITEM,
        DELETE_GUI,
        DELETE_PAGE;

        public static MenuMode parse(String raw) {
            try {
                return valueOf(raw.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException error) {
                throw new IllegalArgumentException("Unknown menu mode: " + raw);
            }
        }

        public String commandName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public static void showMain(ServerPlayerEntity player) {
        List<DialogActionButtonData> actions = List.of(
                runButton("Create GUI", "guimaker menu create",
                        "Create a new GUI profile or append a page."),
                runButton("Edit GUI", "guimaker menu list edit 0",
                        "Select a GUI and page to edit."),
                runButton("Bind Held Item", "guimaker menu list bind_item 0",
                        "Open a GUI by right-clicking or left-clicking the held item."),
                runButton("Delete GUI", "guimaker menu delete",
                        "Delete a GUI profile or one of its pages."),
                runButton("Open GUI", "guimaker menu list open 0",
                        "Open a configured GUI page.")
        );
        open(player, dialog("GUI Maker v2.0 Menu",
                "Choose an operation.", List.of(), actions, 1));
    }

    public static void showCreateMenu(ServerPlayerEntity player) {
        List<DialogActionButtonData> actions = List.of(
                runButton("Create New GUI", "guimaker menu create-form",
                        "Create a new GUI profile with page 0."),
                runButton("Append a Page", "guimaker menu list add_page 0",
                        "Select an existing GUI and add a page."),
                runButton("Back", "guimaker menu", null)
        );
        open(player, dialog("GUI Maker v2.0 Menu - Create GUI",
                "Choose how to create content.", List.of(), actions, 1));
    }

    public static void showCreateForm(ServerPlayerEntity player) {
        List<DialogInput> inputs = List.of(
                textInput("gui", "GUI ID", "new_gui", 32),
                rowsInput(),
                textInput("title", "Page Title", "New GUI", 64)
        );
        List<DialogActionButtonData> actions = List.of(
                dynamicButton("Create", "guimaker create $(gui) $(rows) $(title)",
                        "Create the GUI using these values."),
                runButton("Back", "guimaker menu create", null)
        );
        open(player, dialog("GUI Maker v2.0 Menu - Create GUI",
                "GUI IDs may contain only a-z, 0-9, underscore and hyphen.", inputs, actions, 1));
    }

    public static void showGuiList(ServerPlayerEntity player, MenuMode mode, int listPage) {
        List<GuiDefinition> guis = new ArrayList<>(GuiRepository.all());
        guis.sort(Comparator.comparing(GuiDefinition::id));
        PageSlice<GuiDefinition> slice = slice(guis, listPage);
        List<DialogActionButtonData> actions = new ArrayList<>();

        for (GuiDefinition gui : slice.entries()) {
            actions.add(runButton("GUI: " + gui.id(), commandForGui(mode, gui.id()),
                    gui.pages().size() + " page(s)"));
        }
        addPagination(actions, "guimaker menu list " + mode.commandName(), slice);
        actions.add(runButton("Back", backCommand(mode), null));

        String body = guis.isEmpty()
                ? "No GUI profiles are currently registered."
                : "Choose a GUI profile. Page " + (slice.page() + 1) + " of " + slice.totalPages() + ".";
        open(player, dialog(titleForGuiMode(mode), body, List.of(), actions, 1));
    }

    public static void showPageList(ServerPlayerEntity player, MenuMode mode,
                                    String guiId, int listPage) {
        if (mode != MenuMode.EDIT && mode != MenuMode.OPEN
                && mode != MenuMode.BIND_ITEM && mode != MenuMode.DELETE_PAGE) {
            throw new IllegalArgumentException("This menu mode cannot select pages: " + mode);
        }
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        List<PageDefinition> pages = new ArrayList<>(gui.pages());
        pages.sort(Comparator.comparingInt(PageDefinition::id));
        PageSlice<PageDefinition> slice = slice(pages, listPage);
        List<DialogActionButtonData> actions = new ArrayList<>();

        for (PageDefinition page : slice.entries()) {
            actions.add(runButton("#" + page.id() + " - " + page.title(),
                    commandForPage(mode, gui.id(), page.id()),
                    page.rows() + " row(s), " + page.buttons().size() + " configured slot(s)"));
        }
        addPagination(actions,
                "guimaker menu pages " + mode.commandName() + " " + gui.id(), slice);
        actions.add(runButton("Back", "guimaker menu list " + mode.commandName() + " 0", null));

        open(player, dialog(titleForPageMode(mode),
                "GUI: " + gui.id() + " — choose a page. Page "
                        + (slice.page() + 1) + " of " + slice.totalPages() + ".",
                List.of(), actions, 1));
    }

    public static void showPageOptions(ServerPlayerEntity player, String guiId, int pageId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        String prefix = gui.id() + " " + page.id();
        List<DialogActionButtonData> actions = List.of(
                runButton("Change Page Name", "guimaker menu rename " + prefix,
                        "Change the title displayed at the top of the page."),
                runButton("Edit Display Items", "guimaker edit " + prefix,
                        "Open the visual item editor."),
                runButton("Edit Button Actions", "guimaker menu buttons " + prefix + " 0",
                        "Choose a configured slot and edit its safe action."),
                runButton("Configure Widgets", "guimaker menu widgets " + prefix,
                        "Create toggles, cycles, counters and item holders."),
                runButton("Open Page", "guimaker open " + prefix,
                        "Preview the runtime page."),
                runButton("Back", "guimaker menu pages edit " + gui.id() + " 0", null)
        );
        open(player, dialog("GUI Maker v2.0 - Edit",
                "GUI: " + gui.id() + " | Page: " + page.id() + " | " + page.title(),
                List.of(), actions, 1));
    }

    public static void showRenameForm(ServerPlayerEntity player, String guiId, int pageId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        List<DialogInput> inputs = List.of(
                textInput("title", "Page Title", page.title(), 64)
        );
        List<DialogActionButtonData> actions = List.of(
                dynamicButton("Apply",
                        "guimaker page rename " + gui.id() + " " + page.id() + " $(title)",
                        "Save the new page title."),
                runButton("Back", "guimaker menu page-options " + gui.id() + " " + page.id(), null)
        );
        open(player, dialog("GUI Maker v2.0 - Edit Page Name",
                "Enter a non-empty page title.", inputs, actions, 1));
    }

    public static void showAddPageForm(ServerPlayerEntity player, String guiId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        int suggestedPage = 0;
        while (suggestedPage < 9999 && gui.hasPage(suggestedPage)) {
            suggestedPage++;
        }
        List<DialogInput> inputs = List.of(
                textInput("page", "Page ID", Integer.toString(suggestedPage), 4),
                rowsInput(),
                textInput("title", "Page Title", "New Page", 64)
        );
        List<DialogActionButtonData> actions = List.of(
                dynamicButton("Append Page",
                        "guimaker page add " + gui.id() + " $(page) $(rows) $(title)",
                        "Add the page to GUI " + gui.id() + "."),
                runButton("Back", "guimaker menu list add_page 0", null)
        );
        open(player, dialog("GUI Maker v2.0 Menu - Append a Page",
                "GUI: " + gui.id() + ". Page IDs must be between 0 and 9999.",
                inputs, actions, 1));
    }

    public static void showButtonList(ServerPlayerEntity player, String guiId,
                                      int pageId, int listPage) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        List<java.util.Map.Entry<Integer, ButtonDefinition>> buttons =
                new ArrayList<>(page.buttons().entrySet());
        buttons.sort(java.util.Map.Entry.comparingByKey());
        PageSlice<java.util.Map.Entry<Integer, ButtonDefinition>> slice = slice(buttons, listPage);
        List<DialogActionButtonData> actions = new ArrayList<>();

        for (java.util.Map.Entry<Integer, ButtonDefinition> entry : slice.entries()) {
            int slot = entry.getKey();
            ButtonDefinition button = entry.getValue();
            ItemStack stack = button.displayStack();
            String itemName = stack.isEmpty() ? "No display item" : stack.getName().getString();
            String parameter = button.parameter().isEmpty() ? "" : " | " + button.parameter();
            actions.add(runButton("Slot " + slot + " - " + itemName,
                    "guimaker menu button " + gui.id() + " " + page.id() + " " + slot,
                    "Action: " + button.action().name() + parameter));
        }
        addPagination(actions,
                "guimaker menu buttons " + gui.id() + " " + page.id(), slice);
        actions.add(runButton("Open Visual Item Editor", "guimaker edit " + gui.id() + " " + page.id(),
                "Add or replace display items."));
        actions.add(runButton("Back", "guimaker menu page-options " + gui.id() + " " + page.id(), null));

        String body = buttons.isEmpty()
                ? "This page has no configured slots. Add items with the visual editor first."
                : "Click a slot to edit its action. Page " + (slice.page() + 1)
                        + " of " + slice.totalPages() + ".";
        open(player, dialog("GUI Maker v2.0 Menu - Edit Page Button",
                body, List.of(), actions, 1));
    }

    public static void showButtonConfig(ServerPlayerEntity player, String guiId,
                                        int pageId, int slot) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        ButtonDefinition button = page.button(slot);
        if (button == null) {
            throw new IllegalArgumentException("No button is configured in slot " + slot + ".");
        }

        List<SingleOptionInputControl.Entry> actionEntries = new ArrayList<>();
        for (ButtonAction action : ButtonAction.values()) {
            actionEntries.add(new SingleOptionInputControl.Entry(
                    action.name().toLowerCase(Locale.ROOT),
                    Optional.of(Text.literal(action.name())),
                    action == button.action()));
        }
        List<DialogInput> inputs = List.of(
                new DialogInput("action", new SingleOptionInputControl(
                        300, actionEntries, Text.literal("Safe Action"), true)),
                textInput("parameter", "Action Parameter", button.parameter(), 128)
        );
        String target = gui.id() + " " + page.id() + " " + slot;
        List<DialogActionButtonData> actions = List.of(
                dynamicButton("Apply", "guimaker action set " + target + " $(action) $(parameter)",
                        "Apply the selected allowlisted action."),
                runButton("Clear Action", "guimaker action clear " + target,
                        "Keep the display item but remove its action."),
                runButton("Clear Entire Button", "guimaker item clear " + target,
                        "Remove both the display item and action."),
                runButton("Back", "guimaker menu buttons " + gui.id() + " " + page.id() + " 0", null)
        );
        open(player, dialog("GUI Maker v2.0 - Edit Button Config",
                "Slot " + slot + ". Parameters are validated by the action allowlist.",
                inputs, actions, 1));
    }

    public static void showItemBindingForm(ServerPlayerEntity player, String guiId, int pageId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        List<SingleOptionInputControl.Entry> clickEntries = new ArrayList<>();
        for (ItemGuiBinding.ClickMode mode : ItemGuiBinding.ClickMode.values()) {
            clickEntries.add(new SingleOptionInputControl.Entry(
                    mode.commandName(),
                    Optional.of(Text.literal(mode.name())),
                    mode == ItemGuiBinding.ClickMode.BOTH));
        }
        List<DialogInput> inputs = List.of(
                new DialogInput("click", new SingleOptionInputControl(
                        300, clickEntries, Text.literal("Open On"), true))
        );
        List<DialogActionButtonData> actions = List.of(
                dynamicButton("Bind Held Item",
                        "guimaker item bind " + gui.id() + " " + page.id() + " $(click)",
                        "Store the GUI binding in minecraft:custom_data."),
                runButton("Remove Existing Binding", "guimaker item unbind",
                        "Remove only the GUI Maker binding from the held item."),
                runButton("Back", "guimaker menu pages bind_item " + gui.id() + " 0", null)
        );
        open(player, dialog("GUI Maker v2.0 - Bind Held Item",
                "GUI: " + gui.id() + " | Page: " + page.id()
                        + ". Choose which click opens this page.", inputs, actions, 1));
    }

    public static void showWidgetList(ServerPlayerEntity player, String guiId, int pageId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        List<DialogActionButtonData> actions = new ArrayList<>();
        page.buttons().forEach((slot, button) -> {
            WidgetDefinition widget = button.widget();
            if (widget != null) {
                actions.add(runButton("Slot " + slot + " - " + widget.type(),
                        "guimaker menu widget-detail " + gui.id() + " " + page.id() + " " + slot,
                        widget.scope() + " | key=" + widget.key()));
            }
        });
        actions.add(runButton("Create / Replace Widget",
                "guimaker menu widget-form " + gui.id() + " " + page.id(),
                "Configure a widget with slot, type, scope, key and config."));
        actions.add(runButton("Back", "guimaker menu page-options " + gui.id() + " " + page.id(), null));
        open(player, dialog("GUI Maker - Widgets",
                actions.size() <= 2 ? "No widgets are configured on this page." : "Select a widget to reset or remove it.",
                List.of(), actions, 1));
    }

    public static void showWidgetForm(ServerPlayerEntity player, String guiId, int pageId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        List<SingleOptionInputControl.Entry> types = List.of(
                new SingleOptionInputControl.Entry("toggle", Optional.of(Text.literal("Toggle")), true),
                new SingleOptionInputControl.Entry("cycle", Optional.of(Text.literal("Cycle")), false),
                new SingleOptionInputControl.Entry("counter", Optional.of(Text.literal("Counter")), false),
                new SingleOptionInputControl.Entry("item_holder", Optional.of(Text.literal("Item Holder")), false));
        List<SingleOptionInputControl.Entry> scopes = List.of(
                new SingleOptionInputControl.Entry("player", Optional.of(Text.literal("Per Player")), true),
                new SingleOptionInputControl.Entry("world", Optional.of(Text.literal("Shared World")), false));
        List<DialogInput> inputs = List.of(
                textInput("slot", "Slot (0-" + (page.size() - 1) + ")", "0", 2),
                new DialogInput("type", new SingleOptionInputControl(300, types, Text.literal("Widget Type"), true)),
                new DialogInput("scope", new SingleOptionInputControl(300, scopes, Text.literal("State Scope"), true)),
                textInput("key", "State Key", "example_widget", 64),
                textInput("config", "Config", "false", 256));
        List<DialogActionButtonData> actions = List.of(
                dynamicButton("Save Widget",
                        "guimaker widget set " + gui.id() + " " + page.id()
                                + " $(slot) $(type) $(scope) $(key) $(config)",
                        "Toggle: true/false | Cycle: a,b,c | Counter: min,max,step,default | Holder: ignored"),
                runButton("Back", "guimaker menu widgets " + gui.id() + " " + page.id(), null));
        open(player, dialog("GUI Maker - Configure Widget",
                "Config formats: toggle=true; cycle=red,green,blue; counter=0,10,1,0; item_holder=ignored.",
                inputs, actions, 1));
    }

    public static void showWidgetDetail(ServerPlayerEntity player, String guiId, int pageId, int slot) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        ButtonDefinition button = page.button(slot);
        if (button == null || button.widget() == null) throw new IllegalArgumentException("No widget is configured in that slot.");
        WidgetDefinition widget = button.widget();
        List<DialogActionButtonData> actions = List.of(
                runButton("Reset State", "guimaker widget reset " + gui.id() + " " + page.id() + " " + slot,
                        "Reset this widget to its configured default."),
                runButton("Remove Widget", "guimaker widget clear " + gui.id() + " " + page.id() + " " + slot,
                        "Keep the slot item/action but remove widget behavior."),
                runButton("Back", "guimaker menu widgets " + gui.id() + " " + page.id(), null));
        open(player, dialog("Widget - Slot " + slot,
                "Type: " + widget.type() + " | Scope: " + widget.scope() + " | Key: " + widget.key(),
                List.of(), actions, 1));
    }

    public static void showDeleteMenu(ServerPlayerEntity player) {
        List<DialogActionButtonData> actions = List.of(
                runButton("Delete GUI Profile", "guimaker menu list delete_gui 0",
                        "Delete an entire GUI profile."),
                runButton("Delete Page", "guimaker menu list delete_page 0",
                        "Delete one page from a GUI profile."),
                runButton("Back", "guimaker menu", null)
        );
        open(player, dialog("GUI Maker v2.0 Menu - Delete",
                "Choose what to delete.", List.of(), actions, 1));
    }

    public static void showConfirmGuiDelete(ServerPlayerEntity player, String guiId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        List<DialogActionButtonData> actions = List.of(
                runButton("Confirm Delete GUI", "guimaker delete " + gui.id(),
                        "This cannot be undone."),
                runButton("Cancel", "guimaker menu list delete_gui 0", null)
        );
        open(player, dialog("Delete GUI: " + gui.id(),
                "Delete this GUI profile and all of its pages?", List.of(), actions, 1));
    }

    public static void showConfirmPageDelete(ServerPlayerEntity player, String guiId, int pageId) {
        GuiDefinition gui = GuiRepository.requireGui(guiId);
        PageDefinition page = GuiRepository.requirePage(gui.id(), pageId);
        List<DialogActionButtonData> actions = List.of(
                runButton("Confirm Delete Page",
                        "guimaker page remove " + gui.id() + " " + page.id(),
                        "This cannot be undone."),
                runButton("Cancel", "guimaker menu pages delete_page " + gui.id() + " 0", null)
        );
        open(player, dialog("Delete Page: " + page.id(),
                "GUI: " + gui.id() + " | " + page.title(), List.of(), actions, 1));
    }

    private static String commandForGui(MenuMode mode, String guiId) {
        return switch (mode) {
            case EDIT -> "guimaker menu pages edit " + guiId + " 0";
            case OPEN -> "guimaker menu pages open " + guiId + " 0";
            case ADD_PAGE -> "guimaker menu add-page " + guiId;
            case BIND_ITEM -> "guimaker menu pages bind_item " + guiId + " 0";
            case DELETE_GUI -> "guimaker menu confirm-gui " + guiId;
            case DELETE_PAGE -> "guimaker menu pages delete_page " + guiId + " 0";
        };
    }

    private static String commandForPage(MenuMode mode, String guiId, int pageId) {
        return switch (mode) {
            case EDIT -> "guimaker menu page-options " + guiId + " " + pageId;
            case OPEN -> "guimaker open " + guiId + " " + pageId;
            case BIND_ITEM -> "guimaker menu bind-item " + guiId + " " + pageId;
            case DELETE_PAGE -> "guimaker menu confirm-page " + guiId + " " + pageId;
            default -> throw new IllegalArgumentException("Unsupported page mode: " + mode);
        };
    }

    private static String titleForGuiMode(MenuMode mode) {
        return switch (mode) {
            case EDIT -> "GUI Maker v2.0 Menu - Edit";
            case OPEN -> "GUI Maker v2.0 Menu - Open";
            case ADD_PAGE -> "GUI Maker v2.0 Menu - Append a Page";
            case BIND_ITEM -> "GUI Maker v2.0 Menu - Bind Held Item";
            case DELETE_GUI, DELETE_PAGE -> "GUI Maker v2.0 Menu - Delete";
        };
    }

    private static String titleForPageMode(MenuMode mode) {
        return switch (mode) {
            case EDIT -> "GUI Maker v2.0 Menu - Edit Page";
            case OPEN -> "GUI Maker v2.0 Menu - Open Page";
            case BIND_ITEM -> "GUI Maker v2.0 Menu - Bind Held Item";
            case DELETE_PAGE -> "GUI Maker v2.0 Menu - Delete Page";
            default -> throw new IllegalArgumentException("Unsupported page mode: " + mode);
        };
    }

    private static String backCommand(MenuMode mode) {
        return switch (mode) {
            case ADD_PAGE -> "guimaker menu create";
            case BIND_ITEM -> "guimaker menu";
            case DELETE_GUI, DELETE_PAGE -> "guimaker menu delete";
            case EDIT, OPEN -> "guimaker menu";
        };
    }

    private static DialogInput textInput(String key, String label, String initial, int maxLength) {
        return new DialogInput(key, new TextInputControl(
                300, Text.literal(label), true, initial, maxLength, Optional.empty()));
    }

    private static DialogInput rowsInput() {
        List<SingleOptionInputControl.Entry> entries = new ArrayList<>();
        for (int rows = 1; rows <= 6; rows++) {
            entries.add(new SingleOptionInputControl.Entry(
                    Integer.toString(rows),
                    Optional.of(Text.literal(rows + (rows == 1 ? " row" : " rows"))),
                    rows == 3));
        }
        return new DialogInput("rows", new SingleOptionInputControl(
                300, entries, Text.literal("Rows"), true));
    }

    private static MultiActionDialog dialog(String title, String body,
                                            List<DialogInput> inputs,
                                            List<DialogActionButtonData> actions,
                                            int columns) {
        List<DialogBody> bodies = body == null || body.isBlank()
                ? List.of()
                : List.of(new PlainMessageDialogBody(Text.literal(body), 400));
        DialogCommonData common = new DialogCommonData(
                Text.literal(title).formatted(Formatting.GOLD),
                Optional.empty(),
                true,
                false,
                AfterAction.CLOSE,
                bodies,
                inputs
        );
        return new MultiActionDialog(common, actions, Optional.empty(), columns);
    }

    private static DialogActionButtonData runButton(String label, String command, String tooltip) {
        DialogAction action = new SimpleDialogAction(new ClickEvent.RunCommand(command));
        return actionButton(label, tooltip, action);
    }

    private static DialogActionButtonData dynamicButton(String label, String template, String tooltip) {
        ParsedTemplate parsed = ParsedTemplate.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(template))
                .resultOrPartial(message -> {})
                .orElseThrow(() -> new IllegalArgumentException("Invalid dialog command template"));
        return actionButton(label, tooltip, new DynamicRunCommandDialogAction(parsed));
    }

    private static DialogActionButtonData actionButton(String label, String tooltip, DialogAction action) {
        DialogButtonData data = new DialogButtonData(
                Text.literal(label),
                tooltip == null ? Optional.empty() : Optional.of(Text.literal(tooltip)),
                BUTTON_WIDTH
        );
        return new DialogActionButtonData(data, Optional.of(action));
    }

    private static void addPagination(List<DialogActionButtonData> actions,
                                      String commandPrefix, PageSlice<?> slice) {
        if (slice.page() > 0) {
            actions.add(runButton("Previous", commandPrefix + " " + (slice.page() - 1), null));
        }
        if (slice.page() + 1 < slice.totalPages()) {
            actions.add(runButton("Next", commandPrefix + " " + (slice.page() + 1), null));
        }
    }

    private static <T> PageSlice<T> slice(Collection<T> source, int requestedPage) {
        List<T> values = new ArrayList<>(source);
        int totalPages = Math.max(1, (values.size() + ENTRIES_PER_DIALOG - 1) / ENTRIES_PER_DIALOG);
        int page = Math.max(0, Math.min(requestedPage, totalPages - 1));
        int from = Math.min(page * ENTRIES_PER_DIALOG, values.size());
        int to = Math.min(from + ENTRIES_PER_DIALOG, values.size());
        return new PageSlice<>(values.subList(from, to), page, totalPages);
    }

    private static void open(ServerPlayerEntity player, Dialog dialog) {
        player.openDialog(RegistryEntry.of(dialog));
    }

    private record PageSlice<T>(List<T> entries, int page, int totalPages) {}
}
