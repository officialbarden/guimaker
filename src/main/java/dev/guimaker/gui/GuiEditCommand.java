package dev.guimaker.gui;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.guimaker.action.ButtonAction;
import dev.guimaker.config.LimitsConfig;
import dev.guimaker.config.WorldDataPaths;
import dev.guimaker.data.GuiDefinition;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.data.PageDefinition;
import dev.guimaker.gate.CommandPresetRegistry;
import dev.guimaker.gate.ItemPresetRegistry;
import dev.guimaker.gate.PermissionGate;
import dev.guimaker.gate.SoundRegistry;
import dev.guimaker.item.ItemGuiBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** Registers all creation, editing, persistence and opening commands. */
public final class GuiEditCommand {
    private static final SuggestionProvider<ServerCommandSource> GUI_IDS =
            (context, builder) -> suggest(GuiRepository.ids(), builder.getRemaining(), builder);
    private static final SuggestionProvider<ServerCommandSource> ACTIONS =
            (context, builder) -> suggest(Arrays.stream(ButtonAction.values())
                    .map(value -> value.name().toLowerCase(Locale.ROOT)).toList(),
                    builder.getRemaining(), builder);
    private static final SuggestionProvider<ServerCommandSource> CLICK_MODES =
            (context, builder) -> suggest(Arrays.stream(ItemGuiBinding.ClickMode.values())
                    .map(ItemGuiBinding.ClickMode::commandName).toList(),
                    builder.getRemaining(), builder);
    private static final SuggestionProvider<ServerCommandSource> ACTION_PARAMETERS =
            (context, builder) -> {
                String raw = StringArgumentType.getString(context, "action");
                ButtonAction action = parseAction(raw);
                Collection<String> values = switch (action) {
                    case PLAY_SOUND -> SoundRegistry.ids();
                    case GIVE_ITEM -> ItemPresetRegistry.ids();
                    case RUN_COMMAND -> CommandPresetRegistry.ids();
                    case OPEN_PAGE -> pageIdsFromContext(context);
                    default -> java.util.List.of();
                };
                return suggest(values, builder.getRemaining(), builder);
            };

    private GuiEditCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("guimaker")
                .executes(context -> help(context.getSource()))
                .then(literal("help").executes(context -> help(context.getSource())))
                .then(literal("list").executes(context -> list(context.getSource())))
                .then(GuiMenuCommand.build())
                .then(ManagedDialogCommand.build())
                .then(WidgetCommand.build())
                .then(literal("commands").requires(PermissionGate::canEditGui)
                        .executes(context -> listCommandPresets(context.getSource())))
                .then(literal("limits").requires(PermissionGate::canEditGui)
                        .executes(context -> listLimits(context.getSource())))
                .then(literal("world-data").requires(PermissionGate::canEditGui)
                        .executes(context -> showWorldDataPath(context.getSource())))
                .then(literal("open")
                        .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                .executes(context -> open(context, null))
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> open(context,
                                                IntegerArgumentType.getInteger(context, "page"))))))
                .then(literal("edit").requires(PermissionGate::canEditGui)
                        .executes(GuiEditCommand::editFirst)
                        .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                .executes(context -> edit(context, null))
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> edit(context,
                                                IntegerArgumentType.getInteger(context, "page"))))))
                .then(literal("create").requires(PermissionGate::canEditGui)
                        .then(argument("gui", StringArgumentType.word())
                                .then(argument("rows", IntegerArgumentType.integer(1, 6))
                                        .then(argument("title", StringArgumentType.greedyString())
                                                .executes(GuiEditCommand::create)))))
                .then(literal("delete").requires(PermissionGate::canEditGui)
                        .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                .executes(GuiEditCommand::delete)))
                .then(literal("page").requires(PermissionGate::canEditGui)
                        .then(literal("add")
                                .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                        .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                                .then(argument("rows", IntegerArgumentType.integer(1, 6))
                                                        .then(argument("title", StringArgumentType.greedyString())
                                                                .executes(GuiEditCommand::addPage))))))
                        .then(literal("rename")
                                .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                        .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                                .then(argument("title", StringArgumentType.greedyString())
                                                        .executes(GuiEditCommand::renamePage)))))
                        .then(literal("remove")
                                .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                        .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                                .executes(GuiEditCommand::removePage)))))
                .then(literal("item").requires(PermissionGate::canEditGui)
                        .then(literal("set")
                                .then(slotArguments(GuiEditCommand::setItem)))
                        .then(literal("clear")
                                .then(slotArguments(GuiEditCommand::clearButton)))
                        .then(literal("bind")
                                .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                        .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                                .executes(context -> bindItem(context, ItemGuiBinding.ClickMode.BOTH))
                                                .then(argument("click", StringArgumentType.word()).suggests(CLICK_MODES)
                                                        .executes(context -> bindItem(context,
                                                                ItemGuiBinding.ClickMode.parse(
                                                                        StringArgumentType.getString(context, "click"))))))))
                        .then(literal("unbind").executes(GuiEditCommand::unbindItem))
                        .then(literal("binding").executes(GuiEditCommand::showItemBinding)))
                .then(literal("action").requires(PermissionGate::canEditGui)
                        .then(literal("set")
                                .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                        .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                                .then(argument("slot", IntegerArgumentType.integer(0, 53))
                                                        .then(argument("action", StringArgumentType.word()).suggests(ACTIONS)
                                                                .executes(context -> setAction(context, ""))
                                                                .then(argument("parameter", StringArgumentType.greedyString())
                                                                        .suggests(ACTION_PARAMETERS)
                                                                        .executes(context -> setAction(context,
                                                                                StringArgumentType.getString(context, "parameter")))))))))
                        .then(literal("clear")
                                .then(slotArguments(GuiEditCommand::clearAction))))
                .then(literal("reload").requires(PermissionGate::canEditGui)
                        .executes(GuiEditCommand::reload)));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<ServerCommandSource, String> slotArguments(
            com.mojang.brigadier.Command<ServerCommandSource> command) {
        return argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                        .then(argument("slot", IntegerArgumentType.integer(0, 53))
                                .executes(command)));
    }

    private static int help(ServerCommandSource source) {
        source.sendMessage(Text.literal("GUI Maker commands").formatted(Formatting.GOLD, Formatting.BOLD));
        source.sendMessage(Text.literal("/guimaker list | open <gui> [page]").formatted(Formatting.YELLOW));
        if (PermissionGate.canEditGui(source)) {
            source.sendMessage(Text.literal("/guimaker menu — open the dialog-based operator menu")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker create <gui> <1-6 rows> <title>").formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker edit <gui> [page] — left click: copy held item, right click: clear")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker page add|rename|remove ... | item set|clear ... | action set|clear ...")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker item bind <gui> <page> [right|left|both] | unbind | binding")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker widget set|clear|reset|list ...")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker commands — list gated command presets")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker limits — show limits.json values")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker world-data — show the active save data directory")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("Slot numbers start at 0.").formatted(Formatting.GRAY));
        }
        return 1;
    }

    private static int list(ServerCommandSource source) {
        source.sendMessage(Text.literal("Registered GUIs:").formatted(Formatting.GOLD));
        for (GuiDefinition gui : GuiRepository.all()) {
            String pages = gui.pages().stream().map(page -> Integer.toString(page.id()))
                    .collect(java.util.stream.Collectors.joining(", "));
            source.sendMessage(Text.literal("- " + gui.id() + " (pages: " + pages + ")")
                    .formatted(Formatting.YELLOW));
        }
        return 1;
    }

    private static int listLimits(ServerCommandSource source) {
        source.sendMessage(Text.literal("GUI Maker limits:").formatted(Formatting.GOLD));
        source.sendMessage(Text.literal("- max_guis: " + LimitsConfig.maxGuis())
                .formatted(Formatting.YELLOW));
        source.sendMessage(Text.literal("- max_command_presets: " + LimitsConfig.maxCommandPresets())
                .formatted(Formatting.YELLOW));
        source.sendMessage(Text.literal("Edit <world>/data/guimaker/limits.json and run /guimaker reload to apply changes.")
                .formatted(Formatting.GRAY));
        return 1;
    }

    private static int showWorldDataPath(ServerCommandSource source) {
        source.sendMessage(Text.literal("GUI Maker world data: ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(WorldDataPaths.directory().toString()).formatted(Formatting.YELLOW)));
        return 1;
    }

    private static int listCommandPresets(ServerCommandSource source) {
        source.sendMessage(Text.literal("Gated command presets:").formatted(Formatting.GOLD));
        for (CommandPresetRegistry.CommandPreset preset : CommandPresetRegistry.all()) {
            source.sendMessage(Text.literal("- " + preset.id()
                    + " [as=" + preset.runAs()
                    + ", required_permission=" + preset.requiredPlayerPermissionLevel()
                    + ", server_level=" + preset.serverPermissionLevel()
                    + ", security_ack=" + preset.securityAcknowledged()
                    + ", cooldown=" + preset.cooldownMs() + "ms]")
                    .formatted(Formatting.YELLOW));
            source.sendMessage(Text.literal("  /" + preset.command()).formatted(Formatting.GRAY));
        }
        return 1;
    }

    private static int open(CommandContext<ServerCommandSource> context, Integer requestedPage) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String guiId = StringArgumentType.getString(context, "gui");
        try {
            GuiDefinition gui = GuiRepository.requireGui(guiId);
            int pageId = requestedPage == null ? gui.firstPageId() : requestedPage;
            if (!GuiPageManager.openGui(player, gui.id(), pageId)) {
                return error(context.getSource(), "Page not found: " + pageId);
            }
            return 1;
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int editFirst(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String first = GuiRepository.ids().stream().findFirst().orElse(null);
        if (first == null) {
            return error(context.getSource(), "No GUI is available to edit.");
        }
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        GuiDefinition gui = GuiRepository.requireGui(first);
        GuiPageManager.openEditor(player, gui.id(), gui.firstPageId());
        showEditorInstructions(player);
        return 1;
    }

    private static int edit(CommandContext<ServerCommandSource> context, Integer requestedPage) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String guiId = StringArgumentType.getString(context, "gui");
        try {
            GuiDefinition gui = GuiRepository.requireGui(guiId);
            int pageId = requestedPage == null ? gui.firstPageId() : requestedPage;
            if (!GuiPageManager.openEditor(player, gui.id(), pageId)) {
                return error(context.getSource(), "Page not found: " + pageId);
            }
            showEditorInstructions(player);
            return 1;
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int create(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int rows = IntegerArgumentType.getInteger(context, "rows");
            String title = StringArgumentType.getString(context, "title");
            GuiRepository.create(id, rows, title);
            return success(context.getSource(), "GUI created: " + id + " (page 0)");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int delete(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            GuiRepository.delete(id);
            return success(context.getSource(), "GUI deleted: " + id);
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int addPage(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            int rows = IntegerArgumentType.getInteger(context, "rows");
            String title = StringArgumentType.getString(context, "title");
            GuiRepository.addPage(id, page, rows, title);
            return success(context.getSource(), id + ": page added: " + page);
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int renamePage(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            String title = StringArgumentType.getString(context, "title");
            GuiRepository.renamePage(id, page, title);
            return success(context.getSource(), "Page renamed: " + page);
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int removePage(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            GuiRepository.removePage(id, page);
            return success(context.getSource(), "Page removed: " + page);
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int setItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            int slot = IntegerArgumentType.getInteger(context, "slot");
            ItemStack held = player.getMainHandStack();
            GuiRepository.setItem(id, page, slot, held);
            return success(context.getSource(), "Slot " + slot + " display updated.");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int bindItem(CommandContext<ServerCommandSource> context,
                                ItemGuiBinding.ClickMode clickMode) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            String guiId = StringArgumentType.getString(context, "gui");
            int pageId = IntegerArgumentType.getInteger(context, "page");
            GuiDefinition gui = GuiRepository.requireGui(guiId);
            GuiRepository.requirePage(gui.id(), pageId);
            ItemGuiBinding.bind(player.getMainHandStack(), gui.id(), pageId, clickMode);
            return success(context.getSource(), "Bound the held item to " + gui.id() + " page "
                    + pageId + " for " + clickMode.commandName() + " click.");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int unbindItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            boolean removed = ItemGuiBinding.unbind(player.getMainHandStack());
            return removed
                    ? success(context.getSource(), "Removed the GUI binding from the held item.")
                    : error(context.getSource(), "The held item has no GUI binding.");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int showItemBinding(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return ItemGuiBinding.read(player.getMainHandStack())
                .map(binding -> success(context.getSource(), "Held item binding: GUI=" + binding.guiId()
                        + ", page=" + binding.pageId() + ", click=" + binding.clickMode().commandName()))
                .orElseGet(() -> error(context.getSource(), "The held item has no GUI binding."));
    }

    private static int clearButton(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            int slot = IntegerArgumentType.getInteger(context, "slot");
            GuiRepository.clearButton(id, page, slot);
            return success(context.getSource(), "Slot " + slot + " completely cleared.");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int setAction(CommandContext<ServerCommandSource> context, String parameter) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            int slot = IntegerArgumentType.getInteger(context, "slot");
            ButtonAction action = parseAction(StringArgumentType.getString(context, "action"));
            GuiRepository.setAction(id, page, slot, action, parameter);
            return success(context.getSource(), "Slot " + slot + " action: " + action.name());
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int clearAction(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            int slot = IntegerArgumentType.getInteger(context, "slot");
            GuiRepository.clearAction(id, page, slot);
            return success(context.getSource(), "Slot " + slot + " action cleared.");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        GuiRepository.reload(context.getSource().getServer());
        return success(context.getSource(), "Configuration reloaded from disk.");
    }

    private static ButtonAction parseAction(String raw) {
        String normalized = raw.toUpperCase(Locale.ROOT).replace('-', '_');
        try {
            return ButtonAction.valueOf(normalized);
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("Unknown action: " + raw);
        }
    }

    private static Collection<String> pageIdsFromContext(CommandContext<ServerCommandSource> context) {
        try {
            String guiId = StringArgumentType.getString(context, "gui");
            return GuiRepository.requireGui(guiId).pages().stream()
                    .map(page -> Integer.toString(page.id())).toList();
        } catch (Exception ignored) {
            return java.util.List.of();
        }
    }

    private static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggest(
            Collection<String> candidates, String remaining,
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        String prefix = remaining.toLowerCase(Locale.ROOT);
        for (String candidate : candidates) {
            if (candidate.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                builder.suggest(candidate);
            }
        }
        return builder.buildFuture();
    }

    private static void showEditorInstructions(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("Editor: left click copies the main-hand item; right/shift click clears the slot. "
                + "Set the action with /guimaker action set.").formatted(Formatting.YELLOW), false);
    }

    private static int success(ServerCommandSource source, String message) {
        source.sendFeedback(() -> Text.literal("GUI Maker: " + message).formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int error(ServerCommandSource source, String message) {
        source.sendError(Text.literal("GUI Maker: " + message).formatted(Formatting.RED));
        return 0;
    }
}
