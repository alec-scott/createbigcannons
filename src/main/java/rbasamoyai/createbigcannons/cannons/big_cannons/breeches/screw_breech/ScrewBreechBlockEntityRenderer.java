package rbasamoyai.createbigcannons.cannons.big_cannons.breeches.screw_breech;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.client.AllPartialModels;

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

public class ScrewBreechBlockEntityRenderer extends CBCSafeBlockEntityRenderer<ScrewBreechBlockEntity> {

	public ScrewBreechBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(ScrewBreechBlockEntity te, float partialTicks, PoseStack ms, Parts buffer, int light, int overlay) {
		if (VisualizationManager.supportsVisualization(te.getLevel())) return;

		BlockState blockState = te.getBlockState();
		Direction facing = blockState.getValue(BlockStateProperties.FACING);
		// PORT-NOTE(fabric): was super.renderSafe (Create's kinetic shaft rendering).
		buffer.add(ms, standardKineticRotationTransform(CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, facing), te, light));

		float renderedScrewLockOffset = te.getRenderedBlockOffset(AnimationTickHolder.getPartialTicks());
		float heightOffset = renderedScrewLockOffset * 0.25f;
		float rotationOffset = renderedScrewLockOffset * 360.0f;
		Vector3f normal = facing.step();
		Vector3f height = new Vector3f(normal);
		height.mul(heightOffset);

		Quaternionf q = Axis.of(normal).rotationDegrees(rotationOffset);

		ms.pushPose();

		SuperByteBuffer screwLockRender = CachedBuffers.partialFacing(CBCClientCommon.getScrewBreechForState(blockState), blockState, facing);
		buffer.add(ms, screwLockRender
			.translate(height.x(), height.y(), height.z())
			.rotateCentered(q)
			.light(light));

		ms.popPose();
	}

}
