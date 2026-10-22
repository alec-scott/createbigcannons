package rbasamoyai.createbigcannons.fabric.client;

import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.content.kinetics.base.ShaftVisual;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntityRenderer;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountExtensionBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountVisual;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.YawControllerBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_loading.CannonLoaderBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.AutocannonBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechRenderer;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechVisual;
import rbasamoyai.createbigcannons.cannons.autocannon.recoil_spring.AutocannonRecoilSpringBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.recoil_spring.AutocannonRecoilSpringRenderer;
import rbasamoyai.createbigcannons.cannons.autocannon.recoil_spring.AutocannonRecoilSpringVisual;
import rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlockEntity;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.quickfiring_breech.QuickfiringBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.quickfiring_breech.QuickfiringBreechBlockEntityRenderer;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.quickfiring_breech.QuickfiringBreechVisual;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.screw_breech.ScrewBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.screw_breech.ScrewBreechBlockEntityRenderer;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.screw_breech.ScrewBreechVisual;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechBlockEntityRenderer;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechVisual;
import rbasamoyai.createbigcannons.cannons.big_cannons.cannon_end.BigCannonEndBlockEntity;
import rbasamoyai.createbigcannons.crafting.boring.AbstractCannonDrillBlockEntity;
import rbasamoyai.createbigcannons.crafting.builtup.CannonBuilderBlockEntity;
import rbasamoyai.createbigcannons.crafting.builtup.LayeredBigCannonBlockEntity;
import rbasamoyai.createbigcannons.crafting.builtup.LayeredCannonBlockEntityRenderer;
import rbasamoyai.createbigcannons.crafting.casting.AbstractCannonCastBlockEntity;
import rbasamoyai.createbigcannons.crafting.casting.FinishedCannonCastBlockEntity;
import rbasamoyai.createbigcannons.crafting.casting.FinishedCannonCastBlockEntityRenderer;
import rbasamoyai.createbigcannons.crafting.foundry.BasinFoundryBlockEntity;
import rbasamoyai.createbigcannons.crafting.incomplete.IncompleteAutocannonBlockEntity;
import rbasamoyai.createbigcannons.crafting.incomplete.IncompleteBigCannonBlockEntity;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonProjectileBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntityRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockVisual;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.AbstractFluidShellBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.BigCartridgeBlockEntity;
import com.zurrtum.create.client.AllBlockEntityRenders;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastBlockEntityRenderer;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;

/**
 * Block-entity renderers and Flywheel visuals.
 *
 * <p>PORT-NOTE(fabric): upstream attached these in {@code CBCBlockEntities} with Registrate's {@code .renderer(...)} /
 * {@code .visual(...)}. They are client-only classes, so they are registered here from the client entry point, through
 * Create Fly's {@link AllBlockEntityRenders} helpers ({@code visual(..., false)} = skip the vanilla renderer).</p>
 */
@Environment(EnvType.CLIENT)
public final class CBCBlockEntityRenderers {
	private CBCBlockEntityRenderers() {
	}

	public static void register() {
		AllBlockEntityRenders.visual(CBCBlockEntities.SLIDING_BREECH.get(), SlidingBreechBlockEntityRenderer::new, SlidingBreechVisual::new, be -> true);
		AllBlockEntityRenders.visual(CBCBlockEntities.SCREW_BREECH.get(), ScrewBreechBlockEntityRenderer::new, ScrewBreechVisual.factory(), be -> true);
		AllBlockEntityRenders.visual(CBCBlockEntities.QUICKFIRING_BREECH.get(), QuickfiringBreechBlockEntityRenderer::new, QuickfiringBreechVisual::new, be -> false);
		AllBlockEntityRenders.visual(CBCBlockEntities.CANNON_LOADER.get(), ShaftRenderer::new, ShaftVisual::new, be -> true);
		AllBlockEntityRenders.visual(CBCBlockEntities.CANNON_MOUNT.get(), CannonMountBlockEntityRenderer::new, CannonMountVisual::new, be -> false);
		AllBlockEntityRenders.visual(CBCBlockEntities.YAW_CONTROLLER.get(), ShaftRenderer::new, ShaftVisual::new, be -> false);
		AllBlockEntityRenders.visual(CBCBlockEntities.CANNON_MOUNT_EXTENSION.get(), ShaftRenderer::new, ShaftVisual::new, be -> false);
		AllBlockEntityRenders.visual(CBCBlockEntities.FUZED_BLOCK.get(), FuzedBlockEntityRenderer::new, FuzedBlockVisual::new, be -> false);
		AllBlockEntityRenders.visual(CBCBlockEntities.FLUID_SHELL.get(), FuzedBlockEntityRenderer::new, FuzedBlockVisual::new, be -> false);
		AllBlockEntityRenders.render(CBCBlockEntities.CANNON_CAST.get(), CannonCastBlockEntityRenderer::new);
		AllBlockEntityRenders.render(CBCBlockEntities.FINISHED_CANNON_CAST.get(), FinishedCannonCastBlockEntityRenderer::new);
		AllBlockEntityRenders.visual(CBCBlockEntities.CANNON_DRILL.get(), ShaftRenderer::new, ShaftVisual::new, be -> false);
		AllBlockEntityRenders.visual(CBCBlockEntities.CANNON_BUILDER.get(), ShaftRenderer::new, ShaftVisual::new, be -> false);
		AllBlockEntityRenders.render(CBCBlockEntities.LAYERED_CANNON.get(), LayeredCannonBlockEntityRenderer::new);
		AllBlockEntityRenders.visual(CBCBlockEntities.AUTOCANNON_BREECH.get(), AutocannonBreechRenderer::new, AutocannonBreechVisual::new, be -> false);
		AllBlockEntityRenders.visual(CBCBlockEntities.AUTOCANNON_RECOIL_SPRING.get(), AutocannonRecoilSpringRenderer::new, AutocannonRecoilSpringVisual::new, be -> false);
	}
}
