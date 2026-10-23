package rbasamoyai.createbigcannons.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import com.zurrtum.create.client.content.contraptions.render.OrientedContraptionVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;

import net.minecraft.core.Direction;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

/** Flywheel counterpart of {@link PitchOrientedContraptionEntityRenderer}; see its port note. */
public class PitchOrientedContraptionVisual extends OrientedContraptionVisual<PitchOrientedContraptionEntity> {

	public PitchOrientedContraptionVisual(VisualizationContext ctx, PitchOrientedContraptionEntity entity, float partialTick) {
		super(ctx, entity, partialTick);
	}

	@Override
	public void transform(PoseStack stack, float partialTicks) {
		float yaw = this.entity.getViewYRot(partialTicks) + this.entity.getInitialYaw();
		float pitch = this.entity.getViewXRot(partialTicks);

		stack.translate(-0.5f, 0.0f, -0.5f);
		TransformStack<?> tstack = TransformStack.of(stack)
			.nudge(this.entity.getId())
			.center()
			.rotateYDegrees(yaw);
		if (this.entity.getInitialOrientation().getAxis() == Direction.Axis.X) {
			tstack.rotateZDegrees(pitch);
		} else {
			tstack.rotateXDegrees(pitch);
		}
		tstack.uncenter();
	}

}
