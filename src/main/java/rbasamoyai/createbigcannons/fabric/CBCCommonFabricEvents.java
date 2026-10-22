package rbasamoyai.createbigcannons.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import rbasamoyai.createbigcannons.CBCCommonEvents;
import rbasamoyai.createbigcannons.crafting.boring.CannonDrillBlockEntity;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.FluidShellBlockEntity;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.remix.CBCHasIItemHandlerBlockEntity;

/**
 * Fabric event glue for {@link CBCCommonEvents}.
 *
 * <p>PORT-NOTE(fabric): replaces {@code CBCCommonNeoForgeEvents} (cf. the 1.20.1 branch's {@code CBCCommonFabricEvents}).
 * Deployer recipes are added by {@code mixin.compat.create.DeployerBlockEntityMixin}, since Create Fly has no
 * {@code DeployerRecipeSearchEvent}.</p>
 */
public class CBCCommonFabricEvents {

	public static void register() {
		ServerTickEvents.END_LEVEL_TICK.register(CBCCommonEvents::serverLevelTickEnd);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CBCCommonEvents.onPlayerLogin(handler.getPlayer()));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> CBCCommonEvents.onPlayerLogout(handler.getPlayer()));
		ServerLevelEvents.LOAD.register((server, level) -> CBCCommonEvents.onLoadLevel(level));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> CBCCommonEvents.onDatapackReload(server));
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> CBCCommonEvents.onDatapackSync(player));
		// NeoForge's BreakEvent handler ignored the result (it never cancels), so this always allows the break.
		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
			CBCCommonEvents.onPlayerBreakBlock(state, level, pos, player);
			return true;
		});
		UseBlockCallback.EVENT.register(CBCCommonEvents::onUseItemOnBlock);
		CBCCommonEvents.onAddReloadListeners((listener, id) -> ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id, listener), null);

		FlammableBlockRegistry.getDefaultInstance().add(CBCBlocks.POWDER_CHARGE.get(), 100, 30);

		registerStorages();
	}

	/** PORT-NOTE(fabric): replaces the NeoForge {@code RegisterCapabilitiesEvent} handler (item part). */
	private static void registerStorages() {
		ItemStorage.SIDED.registerForBlockEntities((be, side) -> ((CBCHasIItemHandlerBlockEntity) be).getItemHandler(side),
			CBCBlockEntities.AUTOCANNON_AMMO_CONTAINER.get(),
			CBCBlockEntities.CANNON_MOUNT.get(),
			CBCBlockEntities.CANNON_MOUNT_EXTENSION.get(),
			CBCBlockEntities.FIXED_CANNON_MOUNT.get());
		CannonCastBlockEntity.registerStorage();
		CannonDrillBlockEntity.registerStorage();
		FluidShellBlockEntity.registerStorage();
	}

}
