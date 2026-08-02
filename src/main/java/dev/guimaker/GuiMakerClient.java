package dev.guimaker;

import dev.guimaker.item.ItemGuiBinding;
import dev.guimaker.network.LeftClickAirPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.util.hit.HitResult;

/** Optional client hook that adds left-click-in-air support for bound items. */
public final class GuiMakerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (clickCount == 0 || client.crosshairTarget == null
                    || client.crosshairTarget.getType() != HitResult.Type.MISS) {
                return false;
            }
            var binding = ItemGuiBinding.read(player.getMainHandStack());
            if (binding.isEmpty()
                    || !binding.get().clickMode().accepts(ItemGuiBinding.ClickType.LEFT)
                    || !ClientPlayNetworking.canSend(LeftClickAirPayload.ID)) {
                return false;
            }
            ClientPlayNetworking.send(LeftClickAirPayload.INSTANCE);
            return true;
        });
    }
}
