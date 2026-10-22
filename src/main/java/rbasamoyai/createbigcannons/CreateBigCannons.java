package rbasamoyai.createbigcannons;

import net.minecraft.core.BlockPos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rbasamoyai.createbigcannons.port.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.base.PartialBlockDamageManager;
import rbasamoyai.createbigcannons.cannon_control.cannon_types.CBCCannonContraptionTypes;
import rbasamoyai.createbigcannons.cannon_loading.CBCModifiedContraptionRegistry;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.index.CBCChecks;
import rbasamoyai.createbigcannons.index.CBCDisplaySources;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.index.CBCFluids;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.index.CBCMenuTypes;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.index.CBCRecipeTypes;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.multiloader.IndexPlatform;
import rbasamoyai.createbigcannons.munitions.ProjectileDamageHooks;
import rbasamoyai.createbigcannons.network.CBCRootNetwork;
import rbasamoyai.createbigcannons.remix.CustomExplosion;
import rbasamoyai.createbigcannons.utils.CBCUtils;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect;

public class CreateBigCannons {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "createbigcannons";
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
	public static final PartialBlockDamageManager BLOCK_DAMAGE = new PartialBlockDamageManager();

	public static void init() {
		CBCSoundEvents.prepare();

		CBCMunitionPropertiesHandlers.init();
		ModGroup.register();
        ModGroup.setDefaultTabToNull();
        CBCDisplaySources.register();
		CBCBlocks.register();
		CBCItems.register();
		CBCBlockEntities.register();
		CBCEntityTypes.register();
		CBCMenuTypes.register();
		CBCFluids.register();
		CBCRecipeTypes.register();
		CBCCannonContraptionTypes.register();

		CBCChecks.register();

		CBCRootNetwork.init();
	}

	public static Identifier resource(String path) {
		return CBCUtils.location(MOD_ID, path);
	}

	public static final Identifier SCREEN_SHAKE_HANDLER_ID = resource("shake_handler");

	public static void shakePlayerScreen(ServerPlayer player, ScreenShakeEffect effect) {
		RitchiesProjectileLib.shakePlayerScreen(player, SCREEN_SHAKE_HANDLER_ID, effect);
	}

	public static void handleCustomExplosion(Level level, CustomExplosion.Impl explosion) {
		if (!(level instanceof ServerLevel slevel) || IndexPlatform.onExplosionStart(level, explosion))
			return;

		// PORT-NOTE(mc26.1): explode() now also destroys blocks, so terrain protection is applied inside it (ServerExplosionMixin).
        explosion.setCanDamageTerrain(ProjectileDamageHooks.canDamageTerrain(level, BlockPos.containing(explosion.center())));
		explosion.explode();
		for (ServerPlayer player : slevel.players())
			explosion.sendExplosionToClient(player);
	}

	public static void onCommonSetup() {
		CBCModifiedContraptionRegistry.registerDefaults();
	}

}
