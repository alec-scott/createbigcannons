package rbasamoyai.createbigcannons.fabric.client;

import com.zurrtum.create.client.content.contraptions.render.ContraptionVisual;
import com.zurrtum.create.client.content.contraptions.render.OrientedContraptionEntityRenderer;
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleEntityVisualizer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageRenderer;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonProjectileRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.drop_mortar_shell.DropMortarShellRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.grapeshot.GrapeshotBurstRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.PrimedPropellantRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel.ShrapnelBurstRenderer;

/**
 * Entity renderers and visuals.
 *
 * <p>PORT-NOTE(fabric): these were {@code .renderer(...)}/{@code .visual(...)} calls on the entity entries in
 * {@code CBCEntityTypes}. Create Fly's renderer classes are absent from its server jar, so they are registered here from
 * the client entrypoint instead (cf. Create Fly's {@code AllEntityRenders}).</p>
 */
public class CBCEntityRenderers {

	public static void register() {
		EntityRenderers.register(CBCEntityTypes.PITCH_ORIENTED_CONTRAPTION.get(), OrientedContraptionEntityRenderer::new);
		SimpleEntityVisualizer.builder(CBCEntityTypes.PITCH_ORIENTED_CONTRAPTION.get())
			.factory(ContraptionVisual::new)
			.skipVanillaRender(entity -> false)
			.apply();

		EntityRenderers.register(CBCEntityTypes.SHOT.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.HE_SHELL.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.SHRAPNEL_SHELL.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.BAG_OF_GRAPESHOT.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.AP_SHOT.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.TRAFFIC_CONE.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.AP_SHELL.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.FLUID_SHELL.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.SMOKE_SHELL.get(), BigCannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.MORTAR_STONE.get(), BigCannonProjectileRenderer::new);

		EntityRenderers.register(CBCEntityTypes.DROP_MORTAR_SHELL.get(), DropMortarShellRenderer::new);
		EntityRenderers.register(CBCEntityTypes.SHRAPNEL_BURST.get(), ShrapnelBurstRenderer::new);
		EntityRenderers.register(CBCEntityTypes.FLAK_BURST.get(), ShrapnelBurstRenderer::new);
		EntityRenderers.register(CBCEntityTypes.GRAPESHOT_BURST.get(), GrapeshotBurstRenderer::new);
		EntityRenderers.register(CBCEntityTypes.FLUID_BLOB_BURST.get(), NoopRenderer::new);
		EntityRenderers.register(CBCEntityTypes.SMOKE_EMITTER.get(), NoopRenderer::new);
		EntityRenderers.register(CBCEntityTypes.GAS_CLOUD.get(), NoopRenderer::new);
		EntityRenderers.register(CBCEntityTypes.CANNON_CARRIAGE.get(), CannonCarriageRenderer::new);

		EntityRenderers.register(CBCEntityTypes.AP_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.FLAK_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
		EntityRenderers.register(CBCEntityTypes.MACHINE_GUN_BULLET.get(), AutocannonProjectileRenderer::new);

		EntityRenderers.register(CBCEntityTypes.PRIMED_PROPELLANT.get(), PrimedPropellantRenderer::new);
	}

}
