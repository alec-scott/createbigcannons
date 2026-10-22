package rbasamoyai.createbigcannons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import rbasamoyai.createbigcannons.base.CBCRemovalListener;

/** See {@link CBCRemovalListener}. */
@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

	@Shadow public abstract Level getLevel();

	@WrapOperation(method = "setBlockState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;preRemoveSideEffects(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
	private void createbigcannons$setBlockState$onRemove(BlockEntity blockEntity, BlockPos pos, BlockState oldState, Operation<Void> original) {
		if (oldState.getBlock() instanceof CBCRemovalListener listener)
			listener.onRemove(oldState, this.getLevel(), pos, this.getLevel().getBlockState(pos), false);
		original.call(blockEntity, pos, oldState);
	}

}
