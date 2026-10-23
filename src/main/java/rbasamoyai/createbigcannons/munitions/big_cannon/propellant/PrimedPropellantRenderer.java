package rbasamoyai.createbigcannons.munitions.big_cannon.propellant;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

/** PORT-NOTE(mc26.1): follows vanilla's 26.1 {@link TntRenderer} (render state + submitted white-flash block). */
public class PrimedPropellantRenderer extends EntityRenderer<PrimedPropellant, TntRenderState> {

	private final BlockModelResolver blockModelResolver;

	public PrimedPropellantRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.5f;
		this.blockModelResolver = context.getBlockModelResolver();
	}

	@Override
	public TntRenderState createRenderState() {
		return new TntRenderState();
	}

	@Override
	public void extractRenderState(PrimedPropellant entity, TntRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.fuseRemainingInTicks = entity.getFuse() - partialTicks + 1.0f;
		this.blockModelResolver.update(state.blockState, entity.getAppearance(), TntRenderer.BLOCK_DISPLAY_CONTEXT);
	}

	@Override
	public void submit(TntRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0f, 0.5f, 0f);
		float fuse = state.fuseRemainingInTicks;
		if (fuse < 10f) {
			float f = 1f - fuse / 10f;
			f = Mth.clamp(f, 0f, 1f);
			f *= f;
			f *= f;
			float g = 1f + f * 0.3f;
			poseStack.scale(g, g, g);
		}

		poseStack.mulPose(Axis.YP.rotationDegrees(-90f));
		poseStack.translate(-0.5f, -0.5f, 0.5f);
		poseStack.mulPose(Axis.YP.rotationDegrees(90f));
		if (!state.blockState.isEmpty())
			TntMinecartRenderer.submitWhiteSolidBlock(state.blockState, poseStack, collector, state.lightCoords, (int) fuse / 5 % 2 == 0, state.outlineColor);
		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}

    @Override protected int getBlockLightLevel(PrimedPropellant entity, BlockPos pos) { return 15; }

}
