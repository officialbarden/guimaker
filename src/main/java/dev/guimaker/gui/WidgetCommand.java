package dev.guimaker.gui;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.guimaker.data.ButtonDefinition;
import dev.guimaker.data.GuiRepository;
import dev.guimaker.data.PageDefinition;
import dev.guimaker.gate.PermissionGate;
import dev.guimaker.widget.WidgetDefinition;
import dev.guimaker.widget.WidgetStateRepository;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** Commands for configuring and resetting chest-GUI widgets. */
public final class WidgetCommand {
    private WidgetCommand() {}

    public static LiteralArgumentBuilder<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> root =
                literal("widget").requires(PermissionGate::canEditGui);
        root.then(literal("set").then(setArguments()));
        root.then(literal("clear").then(arguments(WidgetCommand::clear)));
        root.then(literal("reset").then(arguments(WidgetCommand::reset)));
        root.then(literal("list")
                .then(argument("gui", StringArgumentType.word())
                        .then(argument("page", IntegerArgumentType.integer(0, 9999))
                                .executes(WidgetCommand::list))));
        return root;
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<ServerCommandSource, String> setArguments() {
        return argument("gui", StringArgumentType.word())
                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                        .then(argument("slot", IntegerArgumentType.integer(0, 53))
                                .then(argument("type", StringArgumentType.word())
                                        .suggests((c, b) -> suggest(b, "toggle", "cycle", "counter", "item_holder"))
                                        .then(argument("scope", StringArgumentType.word())
                                                .suggests((c, b) -> suggest(b, "player", "world"))
                                                .then(argument("key", StringArgumentType.word())
                                                        .executes(context -> set(context, ""))
                                                        .then(argument("config", StringArgumentType.greedyString())
                                                                .executes(context -> set(context,
                                                                        StringArgumentType.getString(context, "config")))))))));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<ServerCommandSource, String> arguments(
            com.mojang.brigadier.Command<ServerCommandSource> command) {
        return argument("gui", StringArgumentType.word())
                .then(argument("page", IntegerArgumentType.integer(0, 9999))
                        .then(argument("slot", IntegerArgumentType.integer(0, 53)).executes(command)));
    }

    private static int set(CommandContext<ServerCommandSource> context, String config) {
        try {
            String gui = StringArgumentType.getString(context, "gui");
            int page = IntegerArgumentType.getInteger(context, "page");
            int slot = IntegerArgumentType.getInteger(context, "slot");
            String type = StringArgumentType.getString(context, "type").toUpperCase(Locale.ROOT);
            WidgetDefinition.Scope scope = WidgetDefinition.parseScope(StringArgumentType.getString(context, "scope"));
            String key = StringArgumentType.getString(context, "key");
            WidgetDefinition widget = parse(type, scope, key, config);
            GuiRepository.setWidget(gui, page, slot, widget);
            return success(context, "Widget configured: " + widget.type() + " " + widget.scope() + " " + widget.key());
        } catch (Exception error) { return fail(context, error); }
    }

    private static WidgetDefinition parse(String type, WidgetDefinition.Scope scope, String key, String config) {
        return switch (type) {
            case "TOGGLE" -> WidgetDefinition.toggle(scope, key,
                    !config.isBlank() && Boolean.parseBoolean(config.strip()));
            case "CYCLE" -> {
                List<String> options = Arrays.stream(config.split(","))
                        .map(String::strip).filter(value -> !value.isEmpty()).distinct().toList();
                yield WidgetDefinition.cycle(scope, key, options);
            }
            case "COUNTER" -> {
                String[] values = config.split(",");
                if (values.length != 4) throw new IllegalArgumentException("Counter config must be min,max,step,default.");
                yield WidgetDefinition.counter(scope, key,
                        Integer.parseInt(values[0].strip()), Integer.parseInt(values[1].strip()),
                        Integer.parseInt(values[2].strip()), Integer.parseInt(values[3].strip()));
            }
            case "ITEM_HOLDER", "HOLDER" -> WidgetDefinition.itemHolder(scope, key);
            default -> throw new IllegalArgumentException("Widget type must be toggle, cycle, counter or item_holder.");
        };
    }

    private static int clear(CommandContext<ServerCommandSource> context) {
        try {
            GuiRepository.clearWidget(gui(context), page(context), slot(context));
            return success(context, "Widget configuration cleared.");
        } catch (Exception error) { return fail(context, error); }
    }

    private static int reset(CommandContext<ServerCommandSource> context) {
        try {
            ButtonDefinition definition = GuiRepository.requirePage(gui(context), page(context)).button(slot(context));
            if (definition == null || definition.widget() == null) throw new IllegalArgumentException("No widget is configured in that slot.");
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (definition.widget().scope() == WidgetDefinition.Scope.PLAYER && player == null)
                throw new IllegalArgumentException("Player-scoped widget reset must be run by a player.");
            WidgetStateRepository.reset(definition.widget(), player);
            return success(context, "Widget state reset.");
        } catch (Exception error) { return fail(context, error); }
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        try {
            PageDefinition page = GuiRepository.requirePage(gui(context), page(context));
            List<String> widgets = new ArrayList<>();
            page.buttons().forEach((slot, button) -> {
                if (button.widget() != null)
                    widgets.add(slot + ":" + button.widget().type().name().toLowerCase(Locale.ROOT)
                            + ":" + button.widget().scope().name().toLowerCase(Locale.ROOT)
                            + ":" + button.widget().key());
            });
            context.getSource().sendMessage(Text.literal(widgets.isEmpty() ? "No widgets." : String.join(" | ", widgets))
                    .formatted(Formatting.YELLOW));
            return 1;
        } catch (Exception error) { return fail(context, error); }
    }

    private static String gui(CommandContext<ServerCommandSource> c) { return StringArgumentType.getString(c, "gui"); }
    private static int page(CommandContext<ServerCommandSource> c) { return IntegerArgumentType.getInteger(c, "page"); }
    private static int slot(CommandContext<ServerCommandSource> c) { return IntegerArgumentType.getInteger(c, "slot"); }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggest(
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder, String... values) {
        String prefix = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String value : values) if (value.startsWith(prefix)) builder.suggest(value);
        return builder.buildFuture();
    }

    private static int success(CommandContext<ServerCommandSource> c, String message) {
        c.getSource().sendFeedback(() -> Text.literal("GUI Maker: " + message).formatted(Formatting.GREEN), false); return 1;
    }
    private static int fail(CommandContext<ServerCommandSource> c, Exception error) {
        c.getSource().sendError(Text.literal("GUI Maker: " + error.getMessage()).formatted(Formatting.RED)); return 0;
    }
}
