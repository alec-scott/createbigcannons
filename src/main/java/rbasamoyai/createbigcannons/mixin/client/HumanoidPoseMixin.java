package rbasamoyai.createbigcannons.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import rbasamoyai.createbigcannons.fabric.client.CBCRenderStateData;

/**
 * Rider poses: no riding pose on a carriage that isn't the cannon rider, and arms forward when manning a cannon.
 *
 * <p>PORT-NOTE(mc26.1): models animate from render states, so the vehicle is tagged on the state by
 * {@code LivingEntityRendererMixin} ({@link CBCRenderStateData#CBC_VEHICLE}).</p>
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidPoseMixin {

	@Shadow @Final public ModelPart head;
	@Shadow @Final public ModelPart leftArm;
	@Shadow @Final public ModelPart rightArm;

	@WrapMethod(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V")
	private void createbigcannons$setupAnimHead(HumanoidRenderState state, Operation<Void> original) {
		CBCRenderStateData.Vehicle vehicle = state.getDataOrDefault(CBCRenderStateData.CBC_VEHICLE, CBCRenderStateData.Vehicle.NONE);
		if (vehicle == CBCRenderStateData.Vehicle.CARRIAGE)
			state.isPassenger = false;

		original.call(state);

		if (vehicle == CBCRenderStateData.Vehicle.CANNON) {
			this.head.xRot = 0;

			this.leftArm.xRot = -1.25f;
			this.leftArm.yRot = 0.25f;
			this.leftArm.zRot = 0;

			this.rightArm.xRot = -1.25f;
			this.rightArm.yRot = -0.25f;
			this.rightArm.zRot = 0;
		}
	}

}
