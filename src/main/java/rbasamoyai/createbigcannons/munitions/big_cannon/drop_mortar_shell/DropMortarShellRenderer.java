package rbasamoyai.createbigcannons.munitions.big_cannon.drop_mortar_shell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonProjectileRenderer;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/** PORT-NOTE(mc26.1): render state + submit; the tracer quad is shared with {@link BigCannonProjectileRenderer}. */
public class DropMortarShellRenderer extends EntityRenderer<DropMortarShellProjectile, DropMortarShellRenderer.State> {

	public DropMortarShellRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(DropMortarShellProjectile entity, State state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.tracer = entity.hasTracer();
		state.orientation = entity.getOrientation();
		state.tracerFrame = (int) ((entity.getId() + entity.level().getGameTime()) % 4L);
		state.model = CachedBuffers.partial(CBCBlockPartials.DROP_MORTAR_SHELL_FLYING,
				CBCBlocks.DROP_MORTAR_SHELL.getDefaultState().setValue(BlockStateProperties.FACING, Direction.NORTH))
			.light(state.tracer ? LightCoordsUtil.FULL_BRIGHT : state.lightCoords)
			.extractRenderState();
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		Vec3 vel = state.orientation;
		if (vel.lengthSqr() < 1e-4d)
			vel = new Vec3(0, -1, 0);

		poseStack.pushPose();
		if (vel.horizontalDistanceSqr() > 1e-4d && Math.abs(vel.y) > 1e-2d) {
			Vec3 horizontal = new Vec3(vel.x, 0, vel.z).normalize();
			poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize().reverse(), horizontal));
			poseStack.mulPose(CBCUtils.mat4x4fFacing(horizontal));
		} else {
			poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize()));
		}
		if (state.model != null)
			state.model.submit(poseStack, collector);
		poseStack.popPose();

		if (state.tracer)
			BigCannonProjectileRenderer.submitTracer(state.tracerFrame, poseStack, collector, camera);
		super.submit(state, poseStack, collector, camera);
	}

    @Override
    protected int getBlockLightLevel(DropMortarShellProjectile entity, BlockPos pos) {
        return entity.hasTracer() ? 15 : super.getBlockLightLevel(entity, pos);
    }

	public static class State extends EntityRenderState {
		public boolean tracer;
		public int tracerFrame;
		public Vec3 orientation = Vec3.ZERO;
		public SuperByteBufferRenderState model;
	}

}
