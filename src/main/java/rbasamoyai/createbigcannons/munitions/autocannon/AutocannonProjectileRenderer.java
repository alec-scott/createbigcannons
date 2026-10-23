package rbasamoyai.createbigcannons.munitions.autocannon;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class AutocannonProjectileRenderer<T extends AbstractAutocannonProjectile> extends EntityRenderer<T, AutocannonProjectileRenderer.State> {

	private static final Identifier SHRAPNEL_LOCATION = CreateBigCannons.resource("textures/entity/shrapnel.png");
	private static final Identifier COLOR_LOCATION = CreateBigCannons.resource("textures/entity/color.png");
	private static final Identifier COLOR_SPECULAR_LOCATION = CreateBigCannons.resource("textures/entity/color_s.png");
	private static final RenderType SHRAPNEL = RenderTypes.entityCutout(SHRAPNEL_LOCATION);
	private static final RenderType COLOR = RenderTypes.entityTranslucent(COLOR_LOCATION);
	private static final RenderType COLOR_SPECULAR = RenderTypes.eyes(COLOR_SPECULAR_LOCATION);

    public AutocannonProjectileRenderer(EntityRendererProvider.Context context) { super(context); }

    @Override
    public State createRenderState() {
        return new State();
    }

    // PORT-NOTE(mc26.1): render() split into extractRenderState() and submit().
    @Override
    public void extractRenderState(T entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.tracer = entity.isTracer();
        state.machineGun = entity.getAutocannonRoundType() == AutocannonAmmoType.MACHINE_GUN;
        state.orientation = entity.getOrientation();
        if (state.tracer) {
			Vec3 previous = new Vec3(entity.xOld, entity.yOld, entity.zOld);
			Vec3 diff = entity.position().subtract(previous);
			double dlSqr = diff.lengthSqr();
			boolean isFastButNotTeleported = 1e-4d <= dlSqr && dlSqr <= entity.getDeltaMovement().lengthSqr() * 4;
			double diffLength = isFastButNotTeleported ? diff.length() : 0;
			double displacement = entity.getTotalDisplacement() - diffLength * (1 - partialTicks);
			state.length = (float) Math.min(diffLength, displacement);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.tracer) {
			float length = state.length;
			Vec3 vel = state.orientation;
			if (vel.lengthSqr() < 1e-4d)
				vel = new Vec3(0, -1, 0);
			poseStack.pushPose();
			if (vel.horizontalDistanceSqr() > 1e-4d && Math.abs(vel.y) > 1e-2d) {
				Vec3 horizontal = new Vec3(vel.x, 0, vel.z).normalize();
				poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize().reverse(), horizontal));
				poseStack.mulPose(CBCUtils.mat4x4fFacing(horizontal, new Vec3(0, 0, -1)));
			} else {
				poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize(), new Vec3(0, 0, -1)));
			}

			// TODO: config tracer color per projectile?
			float thickness = state.machineGun ? 1 / 32f : 2 / 32f;
			SubmitNodeCollector.CustomGeometryRenderer tracer = (pose, vcons) -> {
				renderBox(vcons, pose.pose(), pose.normal(), 255, 216, 0, length, thickness);
				renderBoxInverted(vcons, pose.pose(), pose.normal(), 255, 80, 0, length, thickness * 1.5f);
			};
			collector.submitCustomGeometry(poseStack, COLOR, tracer);
            if (CBCConfigs.client().enableEmissiveTracers.get())
                collector.submitCustomGeometry(poseStack, COLOR_SPECULAR, tracer);

			poseStack.popPose();
		} else {
			poseStack.pushPose();
			poseStack.mulPose(camera.orientation);
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
			float scale = state.machineGun ? 0.5f : 1f;
			poseStack.scale(scale, scale, scale);

			int packedLight = state.lightCoords;
			collector.submitCustomGeometry(poseStack, SHRAPNEL, (lastPose, builder) -> {
				Matrix4f pose = lastPose.pose();
			vertexShrapnel(builder, pose, packedLight, -0.5f, -0.5f, 0, 1);
			vertexShrapnel(builder, pose, packedLight,  0.5f, -0.5f, 1, 1);
			vertexShrapnel(builder, pose, packedLight,  0.5f,  0.5f, 1, 0);
			vertexShrapnel(builder, pose, packedLight, -0.5f,  0.5f, 0, 0);
			});

			poseStack.popPose();
		}
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustrum, double x, double y, double z) {
        return entity.isTracer() || super.shouldRender(entity, frustrum, x, y, z);
    }

	private static void renderBox(VertexConsumer builder, Matrix4f pose, Matrix3f normal, int r, int g, int b, float length, float thickness) {
		float x1 = -thickness;
		float y1 = -thickness;
		float z1 = -thickness - length;
		float x2 = thickness;
		float y2 = thickness;
		float z2 = thickness;

		// Front
		vertex(builder, pose, r, g, b, x1, y1, z1);
		vertex(builder, pose, r, g, b, x1, y2, z1);
		vertex(builder, pose, r, g, b, x2, y2, z1);
		vertex(builder, pose, r, g, b, x2, y1, z1);

		// Right
		vertex(builder, pose, r, g, b, x1, y1, z1);
		vertex(builder, pose, r, g, b, x1, y1, z2);
		vertex(builder, pose, r, g, b, x1, y2, z2);
		vertex(builder, pose, r, g, b, x1, y2, z1);

		// Back
		vertex(builder, pose, r, g, b, x1, y1, z2);
		vertex(builder, pose, r, g, b, x2, y1, z2);
		vertex(builder, pose, r, g, b, x2, y2, z2);
		vertex(builder, pose, r, g, b, x1, y2, z2);

		// Left
		vertex(builder, pose, r, g, b, x2, y1, z1);
		vertex(builder, pose, r, g, b, x2, y2, z1);
		vertex(builder, pose, r, g, b, x2, y2, z2);
		vertex(builder, pose, r, g, b, x2, y1, z2);

		// Down
		vertex(builder, pose, r, g, b, x2, y1, z2);
		vertex(builder, pose, r, g, b, x1, y1, z2);
		vertex(builder, pose, r, g, b, x1, y1, z1);
		vertex(builder, pose, r, g, b, x2, y1, z1);

		// Up
		vertex(builder, pose, r, g, b, x1, y2, z1);
		vertex(builder, pose, r, g, b, x1, y2, z2);
		vertex(builder, pose, r, g, b, x2, y2, z2);
		vertex(builder, pose, r, g, b, x2, y2, z1);
	}

	private static void renderBoxInverted(VertexConsumer builder, Matrix4f pose, Matrix3f normal, int r, int g, int b, float length, float thickness) {
		float x1 = -thickness;
		float y1 = -thickness;
		float z1 = -thickness - length;
		float x2 = thickness;
		float y2 = thickness;
		float z2 = thickness;

		// Front
		vertex(builder, pose, r, g, b, x1, y1, z1);
		vertex(builder, pose, r, g, b, x2, y1, z1);
		vertex(builder, pose, r, g, b, x2, y2, z1);
		vertex(builder, pose, r, g, b, x1, y2, z1);

		// Right
		vertex(builder, pose, r, g, b, x1, y1, z1);
		vertex(builder, pose, r, g, b, x1, y2, z1);
		vertex(builder, pose, r, g, b, x1, y2, z2);
		vertex(builder, pose, r, g, b, x1, y1, z2);

		// Back
		vertex(builder, pose, r, g, b, x1, y1, z2);
		vertex(builder, pose, r, g, b, x1, y2, z2);
		vertex(builder, pose, r, g, b, x2, y2, z2);
		vertex(builder, pose, r, g, b, x2, y1, z2);

		// Left
		vertex(builder, pose, r, g, b, x2, y1, z1);
		vertex(builder, pose, r, g, b, x2, y1, z2);
		vertex(builder, pose, r, g, b, x2, y2, z2);
		vertex(builder, pose, r, g, b, x2, y2, z1);

		// Down
		vertex(builder, pose, r, g, b, x2, y1, z2);
		vertex(builder, pose, r, g, b, x2, y1, z1);
		vertex(builder, pose, r, g, b, x1, y1, z1);
		vertex(builder, pose, r, g, b, x1, y1, z2);

		// Up
		vertex(builder, pose, r, g, b, x1, y2, z1);
		vertex(builder, pose, r, g, b, x2, y2, z1);
		vertex(builder, pose, r, g, b, x2, y2, z2);
		vertex(builder, pose, r, g, b, x1, y2, z2);
	}

    private static void vertex(VertexConsumer builder, Matrix4f pose, int r, int g, int b, float x, float y, float z) {
        builder.addVertex(pose, x, y, z)
			.setColor(r, g, b, 255)
			.setUv(0, 0)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(LightCoordsUtil.FULL_BRIGHT)
			.setNormal(0, 1, 0);
    }

	private static void vertexShrapnel(VertexConsumer builder, Matrix4f pose, int packedLight, float x, float y, int u, int v) {
		builder.addVertex(pose, x, y, 0.0f)
			.setColor(255, 255, 255, 255)
			.setUv((float) u, (float) v)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(packedLight)
			.setNormal(0.0f, 1.0f, 0.0f);
	}

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return entity.isTracer() ? 15 : super.getBlockLightLevel(entity, pos);
    }

	public static class State extends EntityRenderState {
		public boolean tracer;
		public boolean machineGun;
		public Vec3 orientation = Vec3.ZERO;
		public float length;
	}

}
