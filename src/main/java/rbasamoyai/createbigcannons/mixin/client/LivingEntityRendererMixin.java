package rbasamoyai.createbigcannons.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.fabric.client.CBCRenderStateData;

/** Tags living-entity render states with the CBC vehicle being ridden; read by {@link HumanoidPoseMixin}. */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
	private void createbigcannons$extractRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
		Entity vehicle = entity.getVehicle();
		CBCRenderStateData.Vehicle tag = CBCRenderStateData.Vehicle.NONE;
		if (vehicle instanceof CannonCarriageEntity carriage && !carriage.isCannonRider())
			tag = CBCRenderStateData.Vehicle.CARRIAGE;
		else if (vehicle instanceof PitchOrientedContraptionEntity)
			tag = CBCRenderStateData.Vehicle.CANNON;
		state.setData(CBCRenderStateData.CBC_VEHICLE, tag);
	}

}
