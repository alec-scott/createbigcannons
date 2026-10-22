package rbasamoyai.createbigcannons.fabric;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import rbasamoyai.createbigcannons.network.CBCPacket;

/** PORT-NOTE(fabric): replaces CBCNetworkNeoForge's payload registration. */
public class CBCNetworkFabric {
	public static void init() {
		PayloadTypeRegistry.clientboundPlay().register(CBCPacket.TYPE, CBCPacket.STREAM_CODEC);
		PayloadTypeRegistry.serverboundPlay().register(CBCPacket.TYPE, CBCPacket.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(CBCPacket.TYPE,
			(payload, context) -> payload.pkt().handle(context.server()::execute, context.player().connection, context.player()));
	}
}
