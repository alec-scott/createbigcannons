package rbasamoyai.createbigcannons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import rbasamoyai.createbigcannons.CBCClientCommon;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;
import rbasamoyai.ritchiesprojectilelib.effects.CameraAngleHooks;

/**
 * Fabric client entry point.
 *
 * <p>PORT-NOTE(fabric): replaces {@code CBCClientNeoForge}. NeoForge events without a Fabric API equivalent are mixins
 * in {@code mixin.client}: mouse click/scroll cancelling ({@code MouseHandlerMixin}), FOV ({@code AbstractClientPlayerMixin})
 * and player render transforms. Camera yaw/pitch/roll goes through RPL's {@link CameraAngleHooks} so it combines with
 * RPL's screen shake.</p>
 */
public class CBCClientFabric implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CBCBlockPartials.init();
		CBCClientCommon.onClientSetup();
		CBCClientCommon.registerKeyMappings(KeyMappingHelper::registerKeyMapping);
		CBCClientCommon.registerOverlays("hotbar", (id, element) ->
			HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, CreateBigCannons.resource(id), element));
		CBCClientCommon.registerOverlays("helmet", (id, element) ->
			HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, CreateBigCannons.resource(id), element));
		CBCClientCommon.registerClientReloadListeners((listener, id) ->
			ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(id, listener));
		Minecraft mc = Minecraft.getInstance();
		CBCClientCommon.onRegisterParticleFactories(mc, mc.particleEngine);

		CBCNetworkFabricClient.init();

		ClientTickEvents.START_CLIENT_TICK.register(CBCClientCommon::onClientGameTick);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> CBCClientCommon.onPlayerLogIn(client.player));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> CBCClientCommon.onPlayerLogOut(client.player));
		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> {
			CBCClientCommon.onLoadClientLevel(level);
			if (client.player != null)
				CBCClientCommon.onChangeDimension(client.player);
		});
		CameraAngleHooks.register((camera, partialTicks, modifier) -> CBCClientCommon.onCameraSetup(camera, partialTicks,
			modifier::getYaw, modifier::getPitch, modifier::getRoll, modifier::setYaw, modifier::setPitch, modifier::setRoll));
	}

}
