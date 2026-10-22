package rbasamoyai.createbigcannons.cannons.big_cannons.breeches.screw_breech;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;

import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.createbigcannons.CBCClientCommon;

public class ScrewBreechBlockEntityRenderer extends KineticBlockEntityRenderer<ScrewBreechBlockEntity> {

	public ScrewBreechBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(ScrewBreechBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		if (VisualizationManager.supportsVisualization(te.getLevel())) return;

		BlockState blockState = te.getBlockState();
		Direction facing = blockState.getValue(BlockStateProperties.FACING);

		float renderedScrewLockOffset = te.getRenderedBlockOffset(AnimationTickHolder.getPartialTicks());
		float heightOffset = renderedScrewLockOffset * 0.25f;
		float rotationOffset = renderedScrewLockOffset * 360.0f;
		Vector3f normal = facing.step();
		Vector3f height = new Vector3f(normal);
		height.mul(heightOffset);

		Quaternionf q = Axis.of(normal).rotationDegrees(rotationOffset);

		ms.pushPose();

		SuperByteBuffer screwLockRender = CachedBuffers.partialFacing(CBCClientCommon.getScrewBreechForState(blockState), blockState, facing);
		screwLockRender
			.translate(height.x(), height.y(), height.z())
			.rotateCentered(q)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.solid()));

		ms.popPose();
	}

	@Override
	protected SuperByteBuffer getRotatedModel(ScrewBreechBlockEntity te, BlockState state) {
		return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, state.getValue(BlockStateProperties.FACING));
	}

}
