package rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.ritchiesprojectilelib.projectile_burst.ProjectileBurstRenderer;

/** PORT-NOTE(mc26.1): sub-projectiles are submitted per render state (see RPL's {@link ProjectileBurstRenderer}). */
public class ShrapnelBurstRenderer<T extends ShrapnelBurst> extends ProjectileBurstRenderer<T, ProjectileBurstRenderer.State> {

	private static final Identifier TEXTURE_LOCATION = CreateBigCannons.resource("textures/entity/shrapnel.png");
	private static final RenderType RENDER_TYPE = RenderTypes.entityCutout(TEXTURE_LOCATION);

	public ShrapnelBurstRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ProjectileBurstRenderer.State createRenderState() {
		return new ProjectileBurstRenderer.State();
	}

	protected float subProjectileScale() {
		return 1.0f;
	}

	@Override
	protected void submitSubProjectile(ProjectileBurstRenderer.State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		float scale = this.subProjectileScale();
		poseStack.scale(scale, scale, scale);
		poseStack.mulPose(camera.orientation);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
		int packedLight = state.lightCoords;
		collector.submitCustomGeometry(poseStack, RENDER_TYPE, (lastPose, builder) -> {
			Matrix4f pose = lastPose.pose();
			vertex(builder, pose, packedLight, 0.0f, 0, 0, 1);
			vertex(builder, pose, packedLight, 1.0f, 0, 1, 1);
			vertex(builder, pose, packedLight, 1.0f, 1, 1, 0);
			vertex(builder, pose, packedLight, 0.0f, 1, 0, 0);
		});
		poseStack.popPose();
	}

    private static void vertex(VertexConsumer builder, Matrix4f pose, int packedLight, float x, float y, int u, int v) {
        builder.addVertex(pose, x, y, 0.0f)
            .setColor(255, 255, 255, 255)
            .setUv((float) u, (float) v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(0.0f, 1.0f, 0.0f);
    }

}
