package dev.guimaker.item;

import dev.guimaker.data.GuiRepository;
import dev.guimaker.gate.ItemOpenRateLimitGate;
import dev.guimaker.gui.GuiPageManager;
import dev.guimaker.network.LeftClickAirPayload;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Optional;

/** Opens GUI pages from items carrying a validated custom-data binding. */
public final class ItemGuiInteractionHandler {
    private ItemGuiInteractionHandler() {}

    public static void register() {
        PayloadTypeRegistry.playC2S().register(LeftClickAirPayload.ID, LeftClickAirPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(LeftClickAirPayload.ID, (payload, context) ->
                context.server().execute(() -> openFromStack(
                        context.player(), context.player().getMainHandStack(), ItemGuiBinding.ClickType.LEFT)));

        UseItemCallback.EVENT.register((player, world, hand) ->
                interact(player, world, hand, ItemGuiBinding.ClickType.RIGHT));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
                interact(player, world, hand, ItemGuiBinding.ClickType.RIGHT));
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
                interact(player, world, hand, ItemGuiBinding.ClickType.RIGHT));
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
                interact(player, world, hand, ItemGuiBinding.ClickType.LEFT));
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
                interact(player, world, hand, ItemGuiBinding.ClickType.LEFT));
    }

    private static ActionResult interact(PlayerEntity player, World world, Hand hand,
                                         ItemGuiBinding.ClickType clickType) {
        ItemStack stack = player.getStackInHand(hand);
        Optional<ItemGuiBinding.Binding> binding = ItemGuiBinding.read(stack);
        if (binding.isEmpty() || !binding.get().clickMode().accepts(clickType)) {
            return ActionResult.PASS;
        }

        // SUCCESS on the client cancels the item's normal behavior and causes
        // Fabric to send the corresponding interaction packet to the server.
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            open(serverPlayer, binding.get());
        }
        return ActionResult.SUCCESS_SERVER;
    }

    public static boolean openFromStack(ServerPlayerEntity player, ItemStack stack,
                                        ItemGuiBinding.ClickType clickType) {
        Optional<ItemGuiBinding.Binding> binding = ItemGuiBinding.read(stack);
        if (binding.isEmpty() || !binding.get().clickMode().accepts(clickType)) {
            return false;
        }
        return open(player, binding.get());
    }

    private static boolean open(ServerPlayerEntity player, ItemGuiBinding.Binding binding) {
        if (!ItemOpenRateLimitGate.tryAcquire(player.getUuid())) {
            return false;
        }
        try {
            if (!GuiRepository.requireGui(binding.guiId()).hasPage(binding.pageId())) {
                player.sendMessage(Text.literal("GUI Maker: The bound GUI page no longer exists.")
                        .formatted(Formatting.RED), true);
                return false;
            }
            return GuiPageManager.openGui(player, binding.guiId(), binding.pageId());
        } catch (IllegalArgumentException error) {
            player.sendMessage(Text.literal("GUI Maker: " + error.getMessage())
                    .formatted(Formatting.RED), true);
            return false;
        }
    }
}
