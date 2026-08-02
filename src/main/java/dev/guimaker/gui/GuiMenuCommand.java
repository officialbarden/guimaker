package dev.guimaker.gui;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.gate.PermissionGate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;
import java.util.function.Consumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** Command routing for the vanilla dialog-based operator menu. */
public final class GuiMenuCommand {
    private GuiMenuCommand() {}

    public static LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("menu")
                .requires(PermissionGate::canEditGui)
                .executes(context -> execute(context, DialogMenuManager::showMain))
                .then(literal("create")
                        .executes(context -> execute(context, DialogMenuManager::showCreateMenu)))
                .then(literal("create-form")
                        .executes(context -> execute(context, DialogMenuManager::showCreateForm)))
                .then(literal("delete")
                        .executes(context -> execute(context, DialogMenuManager::showDeleteMenu)))
                .then(literal("list")
                        .then(argument("mode", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                                    for (DialogMenuManager.MenuMode mode : DialogMenuManager.MenuMode.values()) {
                                        if (mode.commandName().startsWith(remaining)) {
                                            builder.suggest(mode.commandName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(argument("list_page", IntegerArgumentType.integer(0))
                                        .executes(context -> execute(context, player ->
                                                DialogMenuManager.showGuiList(player,
                                                        mode(context), integer(context, "list_page")))))))
                .then(literal("pages")
                        .then(argument("mode", StringArgumentType.word())
                                .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                        .then(argument("list_page", IntegerArgumentType.integer(0))
                                                .executes(context -> execute(context, player ->
                                                        DialogMenuManager.showPageList(player,
                                                                mode(context), string(context, "gui"),
                                                                integer(context, "list_page"))))))))
                .then(literal("add-page")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .executes(context -> execute(context, player ->
                                        DialogMenuManager.showAddPageForm(player, string(context, "gui"))))))
                .then(literal("bind-item")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> execute(context, player ->
                                                DialogMenuManager.showItemBindingForm(player,
                                                        string(context, "gui"), integer(context, "page")))))))
                .then(literal("widgets")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> execute(context, player ->
                                                DialogMenuManager.showWidgetList(player,
                                                        string(context, "gui"), integer(context, "page")))))))
                .then(literal("widget-form")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> execute(context, player ->
                                                DialogMenuManager.showWidgetForm(player,
                                                        string(context, "gui"), integer(context, "page")))))))
                .then(literal("widget-detail")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .then(argument("slot", IntegerArgumentType.integer(0, 53))
                                                .executes(context -> execute(context, player ->
                                                        DialogMenuManager.showWidgetDetail(player,
                                                                string(context, "gui"), integer(context, "page"),
                                                                integer(context, "slot"))))))))
                .then(literal("page-options")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> execute(context, player ->
                                                DialogMenuManager.showPageOptions(player,
                                                        string(context, "gui"), integer(context, "page")))))))
                .then(literal("rename")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> execute(context, player ->
                                                DialogMenuManager.showRenameForm(player,
                                                        string(context, "gui"), integer(context, "page")))))))
                .then(literal("buttons")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .then(argument("list_page", IntegerArgumentType.integer(0))
                                                .executes(context -> execute(context, player ->
                                                        DialogMenuManager.showButtonList(player,
                                                                string(context, "gui"), integer(context, "page"),
                                                                integer(context, "list_page"))))))))
                .then(literal("button")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .then(argument("slot", IntegerArgumentType.integer(0, 53))
                                                .executes(context -> execute(context, player ->
                                                        DialogMenuManager.showButtonConfig(player,
                                                                string(context, "gui"), integer(context, "page"),
                                                                integer(context, "slot"))))))))
                .then(literal("confirm-gui")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .executes(context -> execute(context, player ->
                                        DialogMenuManager.showConfirmGuiDelete(player, string(context, "gui"))))))
                .then(literal("confirm-page")
                        .then(argument("gui", StringArgumentType.word()).suggests(GuiMenuCommand::suggestGuiIds)
                                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                        .executes(context -> execute(context, player ->
                                                DialogMenuManager.showConfirmPageDelete(player,
                                                        string(context, "gui"), integer(context, "page")))))));
    }

    private static int execute(CommandContext<ServerCommandSource> context,
                               Consumer<ServerPlayerEntity> operation) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        try {
            operation.accept(player);
            return 1;
        } catch (IllegalArgumentException error) {
            context.getSource().sendError(Text.literal("GUI Maker: " + error.getMessage())
                    .formatted(Formatting.RED));
            return 0;
        }
    }

    private static DialogMenuManager.MenuMode mode(CommandContext<ServerCommandSource> context) {
        return DialogMenuManager.MenuMode.parse(string(context, "mode"));
    }

    private static String string(CommandContext<ServerCommandSource> context, String name) {
        return StringArgumentType.getString(context, name);
    }

    private static int integer(CommandContext<ServerCommandSource> context, String name) {
        return IntegerArgumentType.getInteger(context, name);
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestGuiIds(
            CommandContext<ServerCommandSource> context,
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String id : GuiRepository.ids()) {
            if (id.startsWith(remaining)) {
                builder.suggest(id);
            }
        }
        return builder.buildFuture();
    }
}
