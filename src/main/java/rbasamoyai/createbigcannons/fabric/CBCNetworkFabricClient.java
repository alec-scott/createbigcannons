package rbasamoyai.createbigcannons.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import rbasamoyai.createbigcannons.network.CBCPacket;
import rbasamoyai.createbigcannons.network.RootPacket;

@Environment(EnvType.CLIENT)
public class CBCNetworkFabricClient {
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(CBCPacket.TYPE,
			(payload, context) -> payload.pkt().handle(context.client()::execute, context.client().getConnection(), context.player()));
	}

	public static void sendToServer(RootPacket pkt) {
		ClientPlayNetworking.send(new CBCPacket(pkt));
	}
}
