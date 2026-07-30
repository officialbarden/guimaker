package dev.guimaker.gui;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.guimaker.dialog.DialogRepository;
import dev.guimaker.gate.CommandPresetRegistry;
import dev.guimaker.gate.PermissionGate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** CRUD and editing commands for persistent managed dialogs. */
public final class ManagedDialogCommand {
    private ManagedDialogCommand() {}

    public static LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("dialog")
                .executes(context -> list(context.getSource()))
                .then(literal("list").executes(context -> list(context.getSource())))
                .then(literal("placeholders").executes(context -> placeholders(context.getSource())))
                .then(literal("open")
                        .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                .executes(ManagedDialogCommand::open)))
                .then(literal("create").requires(PermissionGate::canEditGui)
                        .then(argument("id", StringArgumentType.word())
                                .then(argument("type", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestTypes)
                                        .then(argument("title", StringArgumentType.greedyString())
                                                .executes(ManagedDialogCommand::create)))))
                .then(literal("edit").requires(PermissionGate::canEditGui)
                        .then(literal("title")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("value", StringArgumentType.greedyString())
                                                .executes(ManagedDialogCommand::editTitle))))
                        .then(literal("body")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("value", StringArgumentType.greedyString())
                                                .executes(ManagedDialogCommand::editBody))))
                        .then(literal("type")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("type", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestTypes)
                                                .executes(ManagedDialogCommand::editType)))))
                .then(literal("delete").requires(PermissionGate::canEditGui)
                        .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                .executes(ManagedDialogCommand::delete)))
                .then(literal("input").requires(PermissionGate::canEditGui)
                        .then(literal("add")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("key", StringArgumentType.word())
                                                .then(argument("type", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestInputTypes)
                                                        .then(argument("label", StringArgumentType.greedyString())
                                                                .executes(ManagedDialogCommand::addInput))))))
                        .then(literal("remove")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("key", StringArgumentType.word())
                                                .executes(ManagedDialogCommand::removeInput))))
                        .then(literal("option")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("key", StringArgumentType.word())
                                                .then(argument("option", StringArgumentType.word())
                                                        .then(argument("label", StringArgumentType.greedyString())
                                                                .executes(ManagedDialogCommand::addOption)))))))
                .then(literal("wizard").requires(PermissionGate::canEditGui)
                        .then(literal("step")
                                .then(literal("add")
                                        .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                                .then(argument("title", StringArgumentType.greedyString())
                                                        .executes(ManagedDialogCommand::addWizardStep))))
                                .then(literal("remove")
                                        .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                                .then(argument("index", IntegerArgumentType.integer(0))
                                                        .executes(ManagedDialogCommand::removeWizardStep))))))
                .then(literal("action").requires(PermissionGate::canEditGui)
                        .then(literal("add")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("preset", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestPresets)
                                                .then(argument("label", StringArgumentType.greedyString())
                                                        .executes(ManagedDialogCommand::addAction)))))
                        .then(literal("list")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .executes(ManagedDialogCommand::listActions)))
                        .then(literal("remove")
                                .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                        .then(argument("index", IntegerArgumentType.integer(0))
                                                .executes(ManagedDialogCommand::removeAction)))))
                .then(literal("json").requires(PermissionGate::canEditGui)
                        .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                .executes(ManagedDialogCommand::showJson)))
                .then(literal("search")
                        .then(argument("id", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestDialogs)
                                .then(argument("query", StringArgumentType.greedyString())
                                        .executes(ManagedDialogCommand::search))))
                .then(literal("run-preset-input")
                        .then(argument("preset", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestPresets)
                                .then(argument("values", StringArgumentType.greedyString())
                                        .executes(ManagedDialogCommand::runPresetInput))))
                .then(literal("run-preset")
                        .then(argument("preset", StringArgumentType.word()).suggests(ManagedDialogCommand::suggestPresets)
                                .executes(ManagedDialogCommand::runPreset)));
    }

    private static int list(ServerCommandSource source) {
        source.sendMessage(Text.literal("Managed dialogs: " + String.join(", ", DialogRepository.ids()))
                .formatted(Formatting.GOLD));
        return 1;
    }

    private static int placeholders(ServerCommandSource source) {
        source.sendMessage(Text.literal("GUI Maker placeholders: ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(dev.guimaker.util.PlaceholderResolver.supportedPlaceholders())
                        .formatted(Formatting.YELLOW)));
        return 1;
    }

    private static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            player.openDialog(RegistryEntry.of(DialogRepository.get(string(context, "id"), player)));
            return 1;
        } catch (IllegalArgumentException error) {
            return fail(context, error);
        }
    }

    private static int create(CommandContext<ServerCommandSource> context) {
        try {
            DialogRepository.create(string(context, "id"),
                    DialogRepository.ManagedType.parse(string(context, "type")),
                    string(context, "title"));
            return success(context, "Dialog created: " + string(context, "id"));
        } catch (Exception error) { return fail(context, error); }
    }

    private static int editTitle(CommandContext<ServerCommandSource> context) {
        try { DialogRepository.setTitle(string(context, "id"), string(context, "value")); return success(context, "Dialog title updated."); }
        catch (Exception error) { return fail(context, error); }
    }

    private static int editBody(CommandContext<ServerCommandSource> context) {
        try { DialogRepository.setBody(string(context, "id"), string(context, "value")); return success(context, "Dialog body updated."); }
        catch (Exception error) { return fail(context, error); }
    }

    private static int editType(CommandContext<ServerCommandSource> context) {
        try { DialogRepository.setType(string(context, "id"), DialogRepository.ManagedType.parse(string(context, "type"))); return success(context, "Dialog type updated."); }
        catch (Exception error) { return fail(context, error); }
    }

    private static int delete(CommandContext<ServerCommandSource> context) {
        try { DialogRepository.delete(string(context, "id")); return success(context, "Dialog deleted."); }
        catch (Exception error) { return fail(context, error); }
    }

    private static int addInput(CommandContext<ServerCommandSource> context) {
        try {
            DialogRepository.addInput(string(context, "id"), string(context, "key"),
                    DialogRepository.ManagedInputType.parse(string(context, "type")), string(context, "label"));
            return success(context, "Dialog input added.");
        } catch (Exception error) { return fail(context, error); }
    }

    private static int removeInput(CommandContext<ServerCommandSource> context) {
        try { DialogRepository.removeInput(string(context, "id"), string(context, "key")); return success(context, "Dialog input removed."); }
        catch (Exception error) { return fail(context, error); }
    }

    private static int addOption(CommandContext<ServerCommandSource> context) {
        try {
            DialogRepository.addDropdownOption(string(context, "id"), string(context, "key"),
                    string(context, "option"), string(context, "label"));
            return success(context, "Dropdown option added.");
        } catch (Exception error) { return fail(context, error); }
    }

    private static int addWizardStep(CommandContext<ServerCommandSource> context) {
        try { DialogRepository.addWizardStep(string(context, "id"), string(context, "title")); return success(context, "Wizard step added."); }
        catch (Exception error) { return fail(context, error); }
    }

    private static int removeWizardStep(CommandContext<ServerCommandSource> context) {
        try { DialogRepository.removeWizardStep(string(context, "id"), IntegerArgumentType.getInteger(context, "index")); return success(context, "Wizard step removed."); }
        catch (Exception error) { return fail(context, error); }
    }

    private static int addAction(CommandContext<ServerCommandSource> context) {
        try {
            DialogRepository.addPresetAction(string(context, "id"), string(context, "label"), string(context, "preset"));
            return success(context, "Gated preset action added.");
        } catch (Exception error) { return fail(context, error); }
    }

    private static int listActions(CommandContext<ServerCommandSource> context) {
        try {
            var actions = DialogRepository.listActions(string(context, "id"));
            context.getSource().sendMessage(Text.literal(actions.isEmpty()
                    ? "No removable actions." : String.join(" | ", actions)).formatted(Formatting.YELLOW));
            return 1;
        } catch (Exception error) { return fail(context, error); }
    }

    private static int removeAction(CommandContext<ServerCommandSource> context) {
        try {
            DialogRepository.removeAction(string(context, "id"), IntegerArgumentType.getInteger(context, "index"));
            return success(context, "Dialog action removed/reset.");
        } catch (Exception error) { return fail(context, error); }
    }

    private static int showJson(CommandContext<ServerCommandSource> context) {
        try {
            String json = DialogRepository.json(string(context, "id")).toString();
            context.getSource().sendMessage(Text.literal(json.length() > 4000 ? json.substring(0, 4000) + "..." : json));
            return 1;
        } catch (Exception error) { return fail(context, error); }
    }

    private static int search(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            player.openDialog(RegistryEntry.of(DialogRepository.search(string(context, "id"), string(context, "query"), player)));
            return 1;
        } catch (IllegalArgumentException error) { return fail(context, error); }
    }

    private static int runPresetInput(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            com.mojang.brigadier.StringReader reader = new com.mojang.brigadier.StringReader(string(context, "values"));
            java.util.Map<String, String> values = new java.util.LinkedHashMap<>();
            while (reader.canRead()) {
                reader.skipWhitespace();
                if (!reader.canRead()) break;
                String key = reader.readUnquotedString();
                reader.skipWhitespace();
                if (!reader.canRead()) throw new IllegalArgumentException("Missing value for input key: " + key);
                values.put(key, reader.readString());
            }
            return GatedCommandExecutor.execute(context.getSource().getPlayerOrThrow(),
                    string(context, "preset"), values) ? 1 : 0;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException error) {
            return fail(context, error);
        }
    }

    private static int runPreset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return GatedCommandExecutor.execute(context.getSource().getPlayerOrThrow(), string(context, "preset")) ? 1 : 0;
    }

    private static String string(CommandContext<ServerCommandSource> context, String key) {
        return StringArgumentType.getString(context, key);
    }

    private static int success(CommandContext<ServerCommandSource> context, String message) {
        context.getSource().sendFeedback(() -> Text.literal("GUI Maker: " + message).formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int fail(CommandContext<ServerCommandSource> context, Exception error) {
        context.getSource().sendError(Text.literal("GUI Maker: " + error.getMessage()).formatted(Formatting.RED));
        return 0;
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestDialogs(
            CommandContext<ServerCommandSource> context, com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        return suggest(DialogRepository.ids(), builder);
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestPresets(
            CommandContext<ServerCommandSource> context, com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        return suggest(CommandPresetRegistry.ids(), builder);
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestTypes(
            CommandContext<ServerCommandSource> context, com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        return suggest(java.util.Arrays.stream(DialogRepository.ManagedType.values()).map(v -> v.name().toLowerCase(Locale.ROOT)).toList(), builder);
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestInputTypes(
            CommandContext<ServerCommandSource> context, com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        return suggest(java.util.Arrays.stream(DialogRepository.ManagedInputType.values()).map(v -> v.name().toLowerCase(Locale.ROOT)).toList(), builder);
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggest(
            Collection<String> values, com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        String prefix = builder.getRemaining().toLowerCase(Locale.ROOT);
        values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix)).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
