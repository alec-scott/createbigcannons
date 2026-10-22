package rbasamoyai.createbigcannons.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import rbasamoyai.createbigcannons.CreateBigCannons;

/** One payload type carrying every CBC packet, prefixed with its CBC id (see {@link CBCRootNetwork}). */
public record CBCPacket(RootPacket pkt) implements CustomPacketPayload {
	public static final Type<CBCPacket> TYPE = new Type<>(CreateBigCannons.resource("packet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CBCPacket> STREAM_CODEC = CBCRootNetwork.PACKET_STREAM_CODEC
		.map(CBCPacket::new, CBCPacket::pkt);

	@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
