package rbasamoyai.createbigcannons.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.content.equipment.goggles.GoggleOverlayRenderer;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rbasamoyai.createbigcannons.base.goggles.CBCGoggleInfo;
import rbasamoyai.createbigcannons.fabric.client.CBCGoggleTooltip;

/** Adds CBC block entities' {@link CBCGoggleInfo} lines to Create Fly's goggle overlay. */
@Mixin(GoggleOverlayRenderer.class)
public class GoggleOverlayTooltipMixin {

	@WrapOperation(method = "renderOverlay", at = @At(value = "INVOKE",
		target = "Lcom/zurrtum/create/api/behaviour/BlockEntityBehaviour;get(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lcom/zurrtum/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/zurrtum/create/api/behaviour/BlockEntityBehaviour;"))
	private static BlockEntityBehaviour<?> createbigcannons$addCBCGoggleInfo(BlockGetter level, BlockPos pos, BehaviourType<?> type,
																			 Operation<BlockEntityBehaviour<?>> original) {
		BlockEntityBehaviour<?> behaviour = original.call(level, pos, type);
		if (type == TooltipBehaviour.TYPE && level.getBlockEntity(pos) instanceof CBCGoggleInfo info)
			return new CBCGoggleTooltip((TooltipBehaviour<?>) behaviour, info);
		return behaviour;
	}

}
