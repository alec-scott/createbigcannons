package rbasamoyai.createbigcannons.fabric.client.render;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;

/**
 * Base for CBC's block entity renderers.
 *
 * <p>PORT-NOTE(mc26.1): block entity renderers now extract a render state on the client thread and submit geometry
 * later. CBC's renderers were written against the immediate {@code renderSafe(be, pt, ms, buffers, light, overlay)}
 * style (Create's {@code SafeBlockEntityRenderer}), so this base keeps that shape: {@link #renderSafe} runs during
 * extraction against a scratch {@link PoseStack}, and every SuperByteBuffer or custom geometry it emits is recorded with
 * the pose at that moment into {@link Parts}, to be submitted from {@link #submit}.</p>
 */
public abstract class CBCSafeBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, CBCSafeBlockEntityRenderer.State> {

	protected CBCSafeBlockEntityRenderer() {
	}

	protected CBCSafeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}

	// Helpers from Create's KineticBlockEntityRenderer, which Create Fly turned into a render-state renderer.

	protected static BlockState shaft(Direction.Axis axis) {
		return AllBlocks.SHAFT.defaultBlockState().setValue(BlockStateProperties.AXIS, axis);
	}

	protected static Direction.Axis getRotationAxisOf(KineticBlockEntity be) {
		BlockState state = be.getBlockState();
		return state.getBlock() instanceof IRotate rotate ? rotate.getRotationAxis(state) : Direction.Axis.Y;
	}

	protected static float getAngleForBe(KineticBlockEntity be, BlockPos pos, Direction.Axis axis) {
		return KineticBlockEntityRenderer.getAngleForBe(be, pos, axis);
	}

	protected static SuperByteBuffer kineticRotationTransform(SuperByteBuffer buffer, KineticBlockEntity be, Direction.Axis axis, float angle, int light) {
		return buffer.light(light).rotateCentered(angle, axis.getPositive());
	}

	/** A shaft model spinning with {@code be}, as Create's kinetic renderer drew it. */
	protected static SuperByteBuffer standardKineticRotationTransform(SuperByteBuffer buffer, KineticBlockEntity be, int light) {
		Direction.Axis axis = getRotationAxisOf(be);
		return kineticRotationTransform(buffer, be, axis, getAngleForBe(be, be.getBlockPos(), axis), light);
	}

	protected abstract void renderSafe(T be, float partialTicks, PoseStack ms, Parts parts, int light, int overlay);

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(T be, State state, float partialTicks, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumbling) {
		SmartBlockEntityRenderer.extractBase(be, state, crumbling);
		state.parts.clear();
		if (be.getLevel() == null || be.isRemoved())
			return;
		this.renderSafe(be, partialTicks, new PoseStack(), state.parts, state.lightCoords, OverlayTexture.NO_OVERLAY);
	}

	@Override
	public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
		for (Part part : state.parts.iterable())
			part.submit(matrices, queue);
	}

	public static class State extends BlockEntityRenderState {
		public final Parts parts = new Parts();
	}

	public static class Parts {
		private final List<Part> parts = new ArrayList<>();

		/** Records a SuperByteBuffer (with its transforms, light etc.) at the current pose. */
		public void add(PoseStack ms, SuperByteBuffer buffer) {
			this.add(ms, buffer.extractRenderState());
		}

		public void add(PoseStack ms, SuperByteBufferRenderState state) {
			this.parts.add(new BufferPart(ms.last().copy(), state));
		}

		/** Records a SuperByteBuffer drawn with {@code type} instead of its own render type. */
		public void add(PoseStack ms, SuperByteBuffer buffer, RenderType type) {
			this.parts.add(new TypedBufferPart(ms.last().copy(), buffer.extractRenderState(), type));
		}

		/** Records a plain block model (replaces BlockRenderDispatcher#renderSingleBlock). */
		public void block(PoseStack ms, BlockState state, int light) {
			this.add(ms, CachedBuffers.block(state).light(light));
		}

		/** Records raw geometry for {@code type}, drawn at the current pose. */
		public void custom(PoseStack ms, RenderType type, SubmitNodeCollector.CustomGeometryRenderer renderer) {
			this.parts.add(new CustomPart(ms.last().copy(), type, renderer));
		}

		void clear() {
			this.parts.clear();
		}

		Iterable<Part> iterable() {
			return this.parts;
		}
	}

	interface Part {
		void submit(PoseStack matrices, SubmitNodeCollector queue);
	}

	private record BufferPart(PoseStack.Pose pose, SuperByteBufferRenderState state) implements Part {
		@Override
		public void submit(PoseStack matrices, SubmitNodeCollector queue) {
			this.state.submit(this.pose, matrices, queue);
		}
	}

	private record TypedBufferPart(PoseStack.Pose pose, SuperByteBufferRenderState state, RenderType type) implements Part {
		@Override
		public void submit(PoseStack matrices, SubmitNodeCollector queue) {
			matrices.pushPose();
			SuperByteBuffer.mul(matrices.last(), this.pose);
			this.state.submit(this.type, matrices, queue);
			matrices.popPose();
		}
	}

	private record CustomPart(PoseStack.Pose pose, RenderType type, SubmitNodeCollector.CustomGeometryRenderer renderer) implements Part {
		@Override
		public void submit(PoseStack matrices, SubmitNodeCollector queue) {
			matrices.pushPose();
			SuperByteBuffer.mul(matrices.last(), this.pose);
			queue.submitCustomGeometry(matrices, this.type, this.renderer);
			matrices.popPose();
		}
	}

}
