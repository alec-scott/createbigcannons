package rbasamoyai.createbigcannons.cannons.autocannon.recoil_spring;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import java.util.Map;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.createbigcannons.cannons.autocannon.AutocannonBlock;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;

public class AutocannonRecoilSpringRenderer extends CBCSafeBlockEntityRenderer<AutocannonRecoilSpringBlockEntity> {

	public AutocannonRecoilSpringRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(AutocannonRecoilSpringBlockEntity spring, float partialTicks, PoseStack ms, Parts buffer, int light, int overlay) {
		if (VisualizationManager.supportsVisualization(spring.getLevel())) return;

		BlockState state = spring.getBlockState();
		Direction facing = state.getValue(BlockStateProperties.FACING);
		SuperByteBuffer ejectorBuf = CachedBuffers.partialFacing(getPartialModelForState(state), state, facing);

		Direction.Axis axis = facing.getAxis();

		float scale = spring.getAnimateOffset(partialTicks);
		float f1 = scale * 0.5f + 0.5f;

		float fx = axis == Direction.Axis.X ? f1 : 1;
		float fy = axis == Direction.Axis.Y ? f1 : 1;
		float fz = axis == Direction.Axis.Z ? f1 : 1;

		ms.pushPose();

		if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
			ejectorBuf.rotateCentered(Mth.PI, axis.isVertical() ? Direction.EAST : Direction.UP);
		}
		buffer.add(ms, ejectorBuf.scale(fx, fy, fz)
			.light(light));

		Vector3f normal = facing.step();
		normal.mul((1 - scale) * -0.5f);
		for (Map.Entry<BlockPos, BlockState> entry : spring.toAnimate.entrySet()) {
			if (entry.getValue() == null) continue;
			ms.pushPose();
			BlockPos pos = entry.getKey();
			ms.translate(pos.getX() + normal.x(), pos.getY() + normal.y(), pos.getZ() + normal.z());
			buffer.block(ms, entry.getValue(), light);
			ms.popPose();
		}

		ms.popPose();
	}

	private static PartialModel getPartialModelForState(BlockState state) {
		return state.getBlock() instanceof AutocannonBlock cBlock
			? CBCBlockPartials.autocannonSpringFor(cBlock.getAutocannonMaterial())
			: CBCBlockPartials.CAST_IRON_AUTOCANNON_SPRING;
	}

}
