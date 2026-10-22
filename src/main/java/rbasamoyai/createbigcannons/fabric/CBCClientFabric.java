package rbasamoyai.createbigcannons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.KineticAudioBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.server.packs.PackType;
import rbasamoyai.createbigcannons.CBCClientCommon;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.fabric.client.CBCBlockEntityRenderers;
import rbasamoyai.createbigcannons.fabric.client.CBCEntityRenderers;
import rbasamoyai.createbigcannons.fabric.client.CBCConnectedTextures;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;
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
		CBCConnectedTextures.register();
		CBCBlockEntityRenderers.register();
		CBCEntityRenderers.register();
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

		registerKineticBehaviours();
	}

	/**
	 * PORT-NOTE(fabric): Create Fly attaches kinetic goggle info (stress) and machine sounds as client behaviours per
	 * block-entity type, instead of building them into {@code KineticBlockEntity}. Register them for CBC's kinetic types.
	 */
	private static void registerKineticBehaviours() {
		kinetic(CBCBlockEntities.CANNON_LOADER.get());
		kinetic(CBCBlockEntities.CANNON_BUILDER.get());
		kinetic(CBCBlockEntities.CANNON_DRILL.get());
		kinetic(CBCBlockEntities.CANNON_MOUNT.get());
		kinetic(CBCBlockEntities.YAW_CONTROLLER.get());
		kinetic(CBCBlockEntities.CANNON_MOUNT_EXTENSION.get());
		kinetic(CBCBlockEntities.SCREW_BREECH.get());
		kinetic(CBCBlockEntities.SLIDING_BREECH.get());
		kinetic(CBCBlockEntities.BASIN_FOUNDRY.get());
	}

	@SuppressWarnings("unchecked")
	private static <T extends KineticBlockEntity> void kinetic(BlockEntityType<?> type) {
		AllBlockEntityBehaviours.add((BlockEntityType<T>) type, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
	}

}
