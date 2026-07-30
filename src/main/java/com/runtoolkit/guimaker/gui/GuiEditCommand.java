package com.runtoolkit.guimaker.gui;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.runtoolkit.guimaker.action.ButtonAction;
import com.runtoolkit.guimaker.data.GuiDefinition;
import com.runtoolkit.guimaker.data.GuiRepository;
import com.runtoolkit.guimaker.data.PageDefinition;
import com.runtoolkit.guimaker.gate.ItemPresetRegistry;
import com.runtoolkit.guimaker.gate.PermissionGate;
import com.runtoolkit.guimaker.gate.SoundRegistry;
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
    private static final SuggestionProvider<ServerCommandSource> ACTION_PARAMETERS =
            (context, builder) -> {
                String raw = StringArgumentType.getString(context, "action");
                ButtonAction action = parseAction(raw);
                Collection<String> values = switch (action) {
                    case PLAY_SOUND -> SoundRegistry.ids();
                    case GIVE_ITEM -> ItemPresetRegistry.ids();
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
                        .then(literal("remove")
                                .then(argument("gui", StringArgumentType.word()).suggests(GUI_IDS)
                                        .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                                .executes(GuiEditCommand::removePage)))))
                .then(literal("item").requires(PermissionGate::canEditGui)
                        .then(literal("set")
                                .then(slotArguments(GuiEditCommand::setItem)))
                        .then(literal("clear")
                                .then(slotArguments(GuiEditCommand::clearButton))))
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
        source.sendMessage(Text.literal("GUI Maker komutları").formatted(Formatting.GOLD, Formatting.BOLD));
        source.sendMessage(Text.literal("/guimaker list | open <gui> [sayfa]").formatted(Formatting.YELLOW));
        if (PermissionGate.canEditGui(source)) {
            source.sendMessage(Text.literal("/guimaker create <gui> <1-6 satır> <başlık>").formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker edit <gui> [sayfa] — sol tık: eldeki eşyayı kopyala, sağ tık: temizle")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("/guimaker page add|remove ... | item set|clear ... | action set|clear ...")
                    .formatted(Formatting.AQUA));
            source.sendMessage(Text.literal("Slot numaraları 0'dan başlar.").formatted(Formatting.GRAY));
        }
        return 1;
    }

    private static int list(ServerCommandSource source) {
        source.sendMessage(Text.literal("Kayıtlı GUI'ler:").formatted(Formatting.GOLD));
        for (GuiDefinition gui : GuiRepository.all()) {
            String pages = gui.pages().stream().map(page -> Integer.toString(page.id()))
                    .collect(java.util.stream.Collectors.joining(", "));
            source.sendMessage(Text.literal("- " + gui.id() + " (sayfalar: " + pages + ")")
                    .formatted(Formatting.YELLOW));
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
                return error(context.getSource(), "Sayfa bulunamadı: " + pageId);
            }
            return 1;
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int editFirst(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String first = GuiRepository.ids().stream().findFirst().orElse(null);
        if (first == null) {
            return error(context.getSource(), "Düzenlenecek GUI yok.");
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
                return error(context.getSource(), "Sayfa bulunamadı: " + pageId);
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
            return success(context.getSource(), "GUI oluşturuldu: " + id + " (sayfa 0)");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int delete(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            GuiRepository.delete(id);
            return success(context.getSource(), "GUI silindi: " + id);
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
            return success(context.getSource(), id + " GUI'sine sayfa eklendi: " + page);
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int removePage(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            GuiRepository.removePage(id, page);
            return success(context.getSource(), "Sayfa silindi: " + page);
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
            return success(context.getSource(), "Slot " + slot + " görünümü güncellendi.");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int clearButton(CommandContext<ServerCommandSource> context) {
        try {
            String id = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            int slot = IntegerArgumentType.getInteger(context, "slot");
            GuiRepository.clearButton(id, page, slot);
            return success(context.getSource(), "Slot " + slot + " tamamen temizlendi.");
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
            return success(context.getSource(), "Slot " + slot + " eylemi: " + action.name());
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
            return success(context.getSource(), "Slot " + slot + " eylemi temizlendi.");
        } catch (IllegalArgumentException error) {
            return error(context.getSource(), error.getMessage());
        }
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        GuiRepository.reload(context.getSource().getServer());
        return success(context.getSource(), "Yapılandırma diskten yeniden yüklendi.");
    }

    private static ButtonAction parseAction(String raw) {
        String normalized = raw.toUpperCase(Locale.ROOT).replace('-', '_');
        try {
            return ButtonAction.valueOf(normalized);
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("Bilinmeyen eylem: " + raw);
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
        player.sendMessage(Text.literal("Düzenleyici: sol tık ana el eşyasını kopyalar; sağ/shift tık slotu temizler. "
                + "Eylemi /guimaker action set ile ayarlayın.").formatted(Formatting.YELLOW), false);
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
