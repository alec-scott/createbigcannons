package rbasamoyai.createbigcannons.mixin.compat.create;

import net.minecraft.server.level.ServerLevel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.remix.ContraptionRemix;

@Mixin(targets = "com.zurrtum.create.content.contraptions.pulley.PulleyBlock$RopeBlockBase")
public abstract class RopeBlockBaseMixin {

	// PORT-NOTE(mc26.1): onRemove -> affectNeighborsAfterRemoval (server only); runs after the rope's own break handling.
	@WrapMethod(method = "affectNeighborsAfterRemoval")
	private void createbigcannons$onRemove(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving, Operation<Void> original) {
		original.call(state, level, pos, isMoving);
		if (isMoving)
			return;
		ContraptionRemix.removeInnerStateRopes(level, pos.above(), false);
		ContraptionRemix.removeInnerStateRopes(level, pos.below(), false);
	}

}
