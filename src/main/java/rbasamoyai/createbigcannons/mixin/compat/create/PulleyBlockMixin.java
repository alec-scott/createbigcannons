package rbasamoyai.createbigcannons.mixin.compat.create;

import net.minecraft.server.level.ServerLevel;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.zurrtum.create.content.contraptions.pulley.PulleyBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.remix.ContraptionRemix;

@Mixin(PulleyBlock.class)
public abstract class PulleyBlockMixin {

	// PORT-NOTE(mc26.1): onRemove -> affectNeighborsAfterRemoval (server only).
	@WrapMethod(method = "affectNeighborsAfterRemoval")
	private void createbigcannons$onRemove(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving, Operation<Void> original) {
        original.call(state, level, pos, isMoving);
		ContraptionRemix.removeInnerStateRopes(level, pos.below(), isMoving);
	}

}
