package rbasamoyai.createbigcannons.munitions.big_cannon.grapeshot;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel.ShrapnelBurstRenderer;

public class GrapeshotBurstRenderer extends ShrapnelBurstRenderer<GrapeshotBurst> {

	public GrapeshotBurstRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected float subProjectileScale() {
		return 2.0f;
	}

}
