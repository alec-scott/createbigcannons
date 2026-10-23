package rbasamoyai.createbigcannons.munitions.big_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * PORT-NOTE(mc26.1): entity renderers extract a render state and submit geometry. The projectile block is submitted as a
 * moving block (as falling blocks are), so it is lit by the level rather than forced full-bright for tracers.
 */
public class BigCannonProjectileRenderer<T extends AbstractBigCannonProjectile> extends EntityRenderer<T, BigCannonProjectileRenderer.State> {

	public BigCannonProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.5f;
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(T entity, State state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		BlockPos pos = entity.blockPosition();
		state.block.randomSeedPos = pos;
		state.block.blockPos = pos;
		state.block.blockState = entity.getRenderedBlockState();
		if (entity.level() instanceof ClientLevel clientLevel) {
			state.block.biome = clientLevel.getBiome(pos);
			state.block.cardinalLighting = clientLevel.cardinalLighting();
			state.block.lightEngine = clientLevel.getLightEngine();
		}
		state.orientation = entity.getOrientation();
		state.tracer = entity.hasTracer();
		state.tracerFrame = (int) ((entity.getId() + entity.level().getGameTime()) % 4L);
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.block.blockState.getRenderShape() == RenderShape.MODEL) {
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
			poseStack.translate(-0.5, -0.5, -0.5);
			collector.submitMovingBlock(poseStack, state.block);
			poseStack.popPose();
		}
		if (state.tracer)
			submitTracer(state.tracerFrame, poseStack, collector, camera);
		super.submit(state, poseStack, collector, camera);
	}

	public static void submitTracer(int frame, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		Identifier textureLoc = CreateBigCannons.resource(String.format("textures/entity/tracer_glow%d.png", frame));
		RenderType renderType = RenderTypes.entityTranslucent(textureLoc);

		poseStack.pushPose();
		poseStack.mulPose(camera.orientation);
		poseStack.scale(1.5f, 1.5f, 1.5f);
		collector.submitCustomGeometry(poseStack, renderType, BigCannonProjectileRenderer::tracerQuad);
		if (CBCConfigs.client().enableEmissiveTracers.get()) {
			Identifier textureSpecularLoc = CreateBigCannons.resource(String.format("textures/entity/tracer_glow%d_s.png", frame));
			collector.submitCustomGeometry(poseStack, RenderTypes.eyes(textureSpecularLoc), BigCannonProjectileRenderer::tracerQuad);
		}
		poseStack.popPose();
	}

	private static void tracerQuad(PoseStack.Pose pose, VertexConsumer builder) {
		vertex(builder, pose, -0.5f, -0.5f, 0, 1);
		vertex(builder, pose,  0.5f, -0.5f, 1, 1);
		vertex(builder, pose,  0.5f,  0.5f, 1, 0);
		vertex(builder, pose, -0.5f,  0.5f, 0, 0);
	}

	@Override
	public boolean shouldRender(T entity, Frustum camera, double camX, double camY, double camZ) {
		return entity.hasTracer() || super.shouldRender(entity, camera, camX, camY, camZ);
	}

	@Override
	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return entity.hasTracer() ? 15 : super.getBlockLightLevel(entity, pos);
	}

	private static void vertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, int u, int v) {
		builder.addVertex(pose, x, y, 0.0f)
			.setColor(255, 255, 255, 255)
			.setUv((float) u, (float) v)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(LightCoordsUtil.FULL_BRIGHT)
			.setNormal(pose, 0.0f, 1.0f, 0.0f);
	}

	public static class State extends EntityRenderState {
		public final MovingBlockRenderState block = new MovingBlockRenderState();
		public Vec3 orientation = Vec3.ZERO;
		public boolean tracer;
		public int tracerFrame;
	}

}
