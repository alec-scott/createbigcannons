package rbasamoyai.createbigcannons.multiloader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import rbasamoyai.createbigcannons.fabric.CBCNetworkFabricClient;
import rbasamoyai.createbigcannons.network.CBCPacket;
import rbasamoyai.createbigcannons.network.RootPacket;

/** PORT-NOTE(fabric): Fabric networking instead of NeoForge's PacketDistributor. */
public class NetworkPlatform {

	@Environment(EnvType.CLIENT)
	public static void sendToServer(RootPacket pkt) {
		CBCNetworkFabricClient.sendToServer(pkt);
	}

	public static void sendToClientPlayer(RootPacket pkt, ServerPlayer player) {
		ServerPlayNetworking.send(player, new CBCPacket(pkt));
	}

	public static void sendToClientTracking(RootPacket pkt, Entity tracked) {
		CBCPacket payload = new CBCPacket(pkt);
		for (ServerPlayer player : PlayerLookup.tracking(tracked))
			ServerPlayNetworking.send(player, payload);
	}

	public static void sendToClientAll(RootPacket pkt, MinecraftServer server) {
		CBCPacket payload = new CBCPacket(pkt);
		for (ServerPlayer player : PlayerLookup.all(server))
			ServerPlayNetworking.send(player, payload);
	}

}
