package rbasamoyai.createbigcannons.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.contraptions.render.ContraptionEntityRenderer.AbstractContraptionState;
import com.zurrtum.create.client.content.contraptions.render.OrientedContraptionEntityRenderer;
import com.zurrtum.create.content.contraptions.OrientedContraptionEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

/**
 * PORT-NOTE(fabric): replaces {@code mixin.client.PitchOrientedContraptionEntityMixin}. Create Fly moved the contraption
 * render transform from the entity ({@code applyLocalTransforms}) into the renderer and the visual, so CBC's cannon
 * transform (pitch about the cannon's own axis) lives here and in {@link PitchOrientedContraptionVisual}.
 */
public class PitchOrientedContraptionEntityRenderer extends OrientedContraptionEntityRenderer<PitchOrientedContraptionEntity, AbstractContraptionState> {

	public PitchOrientedContraptionEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Pose createTransform(OrientedContraptionEntity entity, float partialTicks) {
		Pose pose = new Pose();
		pose.translate(-0.5f, 0.0f, -0.5f);
		SuperByteBuffer.nudge(pose, entity.getId());
		pose.translate(0.5f, 0.5f, 0.5f);
		float yaw = entity.getViewYRot(partialTicks) + entity.getInitialYaw();
		float pitch = entity.getViewXRot(partialTicks);
		pose.rotate(Axis.YP.rotation(Mth.DEG_TO_RAD * yaw));
		if (entity.getInitialOrientation().getAxis() == Direction.Axis.X) {
			pose.rotate(Axis.ZP.rotation(Mth.DEG_TO_RAD * pitch));
		} else {
			pose.rotate(Axis.XP.rotation(Mth.DEG_TO_RAD * pitch));
		}
		pose.translate(-0.5f, -0.5f, -0.5f);
		return pose;
	}

}
