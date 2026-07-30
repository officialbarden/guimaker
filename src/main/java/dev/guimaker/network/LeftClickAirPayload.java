package dev.guimaker.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Optional client-to-server signal for a bound-item left click in empty air. */
public record LeftClickAirPayload() implements CustomPayload {
    public static final LeftClickAirPayload INSTANCE = new LeftClickAirPayload();
    public static final Id<LeftClickAirPayload> ID =
            new Id<>(Identifier.of("guimaker", "left_click_air"));
    public static final PacketCodec<RegistryByteBuf, LeftClickAirPayload> CODEC =
            PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
