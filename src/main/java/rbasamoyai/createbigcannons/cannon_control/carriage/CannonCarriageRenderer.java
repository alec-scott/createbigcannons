package rbasamoyai.createbigcannons.cannon_control.carriage;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;
import rbasamoyai.createbigcannons.index.CBCBlocks;

/** PORT-NOTE(mc26.1): the partials are extracted into render states and submitted (Create Fly SuperByteBuffer API). */
public class CannonCarriageRenderer extends EntityRenderer<CannonCarriageEntity, CannonCarriageRenderer.State> {

	public CannonCarriageRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(CannonCarriageEntity carriage, State state, float partialTicks) {
		super.extractRenderState(carriage, state, partialTicks);
		state.parts.clear();
		int light = state.lightCoords;
		state.yaw = carriage.getYRot(partialTicks);

		boolean cannonRider = carriage.isCannonRider();
		BlockState blockState = CBCBlocks.CANNON_CARRIAGE.getDefaultState().setValue(CannonCarriageBlock.SADDLED, cannonRider);

		state.parts.add(CachedBuffers.partial(cannonRider ? CBCBlockPartials.CANNON_CARRIAGE_SADDLE : CBCBlockPartials.CANNON_CARRIAGE, blockState)
			.translate(-0.5, 0, -0.5)
			.light(light)
			.extractRenderState());

		state.parts.add(CachedBuffers.partial(CBCBlockPartials.CANNON_CARRIAGE_AXLE, blockState)
			.translate(-0.5, 27 / 32f, -0.5)
			.rotateCentered(carriage.getXRot() * Mth.DEG_TO_RAD, Direction.EAST)
			.light(light)
			.extractRenderState());

		Vector4f oldWheel = carriage.previousWheelState;
		Vector4f newWheel = carriage.getWheelState();
		state.parts.add(wheel(blockState, light, -11 / 16f, -5 / 32f, 90, Mth.lerp(partialTicks, oldWheel.x(), newWheel.x())));
		state.parts.add(wheel(blockState, light, -11 / 16f, 37 / 32f, 90, Mth.lerp(partialTicks, oldWheel.y(), newWheel.y())));
		state.parts.add(wheel(blockState, light, 11 / 16f, -5 / 32f, -90, Mth.lerp(partialTicks, oldWheel.z(), newWheel.z())));
		state.parts.add(wheel(blockState, light, 11 / 16f, 37 / 32f, -90, Mth.lerp(partialTicks, oldWheel.w(), newWheel.w())));
	}

	private static SuperByteBufferRenderState wheel(BlockState blockState, int light, float x, float z, float zRot, float spin) {
		SuperByteBuffer buf = CachedBuffers.partial(CBCBlockPartials.CANNON_CARRIAGE_WHEEL, blockState);
		return buf.translate(x, 0.25, z)
			.rotateXDegrees(90)
			.rotateZDegrees(zRot)
			.rotateYDegrees(spin)
			.light(light)
			.extractRenderState();
	}

	@Override
	public void submit(State state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(180 - state.yaw));
		for (SuperByteBufferRenderState part : state.parts)
			part.submit(stack, collector);
		stack.popPose();
		super.submit(state, stack, collector, camera);
	}

	public static class State extends EntityRenderState {
		public final List<SuperByteBufferRenderState> parts = new ArrayList<>();
		public float yaw;
	}

}
