package rbasamoyai.createbigcannons.cannon_control.cannon_mount;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;


import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;

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
import rbasamoyai.createbigcannons.index.CBCBlockPartials;

public class CannonMountBlockEntityRenderer extends CBCSafeBlockEntityRenderer<CannonMountBlockEntity> {

	public CannonMountBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(CannonMountBlockEntity be, float partialTicks, PoseStack ms, Parts buffer, int light, int overlay) {
		if (VisualizationManager.supportsVisualization(be.getLevel())) return;

		BlockState state = be.getBlockState();
		Direction vertical = state.getValue(BlockStateProperties.VERTICAL_DIRECTION);
		boolean upsideDown = vertical == Direction.UP;


		ms.pushPose();

		SuperByteBuffer yawShaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, vertical);
		KineticBlockEntity yawInterface = be.getYawInterface();
		buffer.add(ms, kineticRotationTransform(yawShaft, yawInterface, Direction.Axis.Y, getAngleForBe(yawInterface, be.getBlockPos(), Direction.Axis.Y), light));

		Direction.Axis pitchAxis = ((IRotate) state.getBlock()).getRotationAxis(state);
		SuperByteBuffer pitchShaft = CachedBuffers.block(shaft(pitchAxis));
		KineticBlockEntity pitchInterface = be.getPitchInterface();
		buffer.add(ms, kineticRotationTransform(pitchShaft, pitchInterface, pitchAxis, getAngleForBe(pitchInterface, be.getBlockPos(), pitchAxis), light));

		float yaw = getMountYaw(be);
		Quaternionf qyaw = upsideDown ? Axis.ZP.rotationDegrees(180).mul(Axis.YP.rotationDegrees(yaw)) : Axis.YP.rotationDegrees(-yaw);
		buffer.add(ms, CachedBuffers.partial(CBCBlockPartials.ROTATING_MOUNT, state)
			.translate(0.0d, upsideDown ? -1.0d : 1.0d, 0.0d)
			.light(light)
			.rotateCentered(qyaw));

		float pitch = be.getPitchOffset(partialTicks - 1);
		Quaternionf qpitch = upsideDown ? Axis.XP.rotationDegrees(pitch) : Axis.XP.rotationDegrees(-pitch);
		Quaternionf qyaw1 = new Quaternionf(qyaw);
		qyaw1.mul(qpitch);
		buffer.add(ms, CachedBuffers.partialFacing(CBCBlockPartials.CANNON_CARRIAGE_AXLE, state, Direction.NORTH)
			.translate(0, upsideDown ? -2.0d : 2.0d, 0)
			.rotateCentered(qyaw1)
			.light(light));

		ms.popPose();
	}

	private static float getMountYaw(CannonMountBlockEntity cmbe) {
		float time = AnimationTickHolder.getPartialTicks(cmbe.getLevel());
		return cmbe.getYawOffset(time - 1);
	}

}
