package rbasamoyai.createbigcannons.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import rbasamoyai.createbigcannons.CBCMods;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.ModGroup;
import rbasamoyai.createbigcannons.base.CBCRegistries;
import rbasamoyai.createbigcannons.cannon_control.config.DefaultCannonMountPropertiesSerializers;
import rbasamoyai.createbigcannons.compat.create.DefaultCreateCompat;
import rbasamoyai.createbigcannons.compat.sable.SableCompat;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.equipment.gas_mask.GasMaskItem;
import rbasamoyai.createbigcannons.block_armor_properties.BlockArmorInspectionToolItem;
import rbasamoyai.createbigcannons.index.CBCArmInteractionPointTypes;
import rbasamoyai.createbigcannons.index.CBCContraptionTypes;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCParticleTypes;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.DefaultFluidCompat;
import rbasamoyai.createbigcannons.port.create.AllSoundEvents;

/**
 * Fabric entry point.
 *
 * <p>PORT-NOTE(fabric): replaces {@code CreateBigCannonsNeoForge}. Fabric has no mod event bus or common-setup phase, so
 * registration runs in order here and the former {@code FMLCommonSetupEvent} work runs at the end.</p>
 */
public class CreateBigCannonsFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		CBCRegistries.init();
		CreateBigCannons.init();
		CBCParticleTypes.register();
		CBCSoundEvents.register(AllSoundEvents.SoundEntry::register);
		CreateBigCannons.REGISTRATE.register();
		ModGroup.registerTabs();

		CBCContraptionTypes.init();
		CBCArmInteractionPointTypes.init();
		CBCDataComponents.init();

		CBCConfigs.register((type, spec) -> ConfigRegistry.INSTANCE.register(CreateBigCannons.MOD_ID, type, spec));
		ModConfigEvents.loading(CreateBigCannons.MOD_ID).register(CBCConfigs::onLoad);
		ModConfigEvents.reloading(CreateBigCannons.MOD_ID).register(CBCConfigs::onReload);

		CBCNetworkFabric.init();
		CBCCommonFabricEvents.register();

		// Former FMLCommonSetupEvent work
		BlockArmorInspectionToolItem.registerDefaultHandlers();
		GasMaskItem.registerDefaultHandlers();
		DefaultFluidCompat.registerMinecraftBlobEffects();
		DefaultFluidCompat.registerCreateBlobEffects();
		CreateBigCannons.onCommonSetup();
		DefaultCreateCompat.init();
		DefaultCannonMountPropertiesSerializers.init();
		CBCMods.SABLE.executeIfInstalled(() -> SableCompat::onCommonSetup);
	}

}
