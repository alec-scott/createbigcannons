package rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.createbigcannons.CBCClientCommon;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.quickfiring_breech.QuickfiringBreechBlock;

public class SlidingBreechBlockEntityRenderer extends CBCSafeBlockEntityRenderer<SlidingBreechBlockEntity> {

	public SlidingBreechBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(SlidingBreechBlockEntity te, float partialTicks, PoseStack ms, Parts buffer, int light, int overlay) {
		BlockState blockState = te.getBlockState();

		if (VisualizationManager.supportsVisualization(te.getLevel())) return;
		// PORT-NOTE(fabric): was super.renderSafe (Create's kinetic shaft rendering).
		buffer.add(ms, standardKineticRotationTransform(CachedBuffers.block(shaft(getRotationAxisOf(te))), te, light));

		Direction facing = blockState.getValue(BlockStateProperties.FACING);
		Direction.Axis axis = CBCClientCommon.getRotationAxis(blockState);
		Direction blockRotation = facing.getCounterClockWise(axis);
		if (blockRotation == Direction.DOWN) blockRotation = Direction.UP;

		Quaternionf qrot;

		boolean alongFirst = blockState.getValue(QuickfiringBreechBlock.AXIS);
		if (facing.getAxis().isHorizontal() && !alongFirst) {
			Direction rotDir = facing.getAxis() == Direction.Axis.X ? Direction.UP : Direction.EAST;
			qrot = Axis.of(rotDir.step()).rotationDegrees(90f);
		} else if (facing.getAxis() == Direction.Axis.X && alongFirst) {
			qrot = Axis.of(blockRotation.step()).rotationDegrees(90f);
		} else {
			qrot = Axis.of(blockRotation.step()).rotationDegrees(0);
		}

		float renderedBreechblockOffset = te.getRenderedBlockOffset(partialTicks);
		renderedBreechblockOffset = renderedBreechblockOffset / 16.0f * 13.0f;
		Vector3f normal = blockRotation.step();
		normal.mul(renderedBreechblockOffset);

		ms.pushPose();
		buffer.add(ms, CachedBuffers.partialFacing(CBCClientCommon.getBreechblockForState(blockState), blockState, blockRotation)
			.translate(normal.x(), normal.y(), normal.z())
			.rotateCentered(qrot)
			.light(light));

		ms.popPose();
	}

}
