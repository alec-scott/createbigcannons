package rbasamoyai.createbigcannons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.base.CBCDynamicFlammableBlock;
import rbasamoyai.createbigcannons.base.CaughtFireBlock;

/** PORT-NOTE(fabric): ported from upstream's 1.20.1 Fabric {@code FireBlockMixin}; see {@link CaughtFireBlock}. */
@Mixin(FireBlock.class)
public class FireBlockMixin {

	@ModifyExpressionValue(method = "checkBurnOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1))
	private BlockState createbigcannons$checkBurnOut$catchFire(BlockState original, @Local(argsOnly = true) Level level,
															   @Local(argsOnly = true) BlockPos pos) {
		if (original.getBlock() instanceof CaughtFireBlock caughtFire)
			caughtFire.onCaughtFire(original, level, pos, null, null);
		return original;
	}

	@WrapOperation(method = "checkBurnOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;getBurnOdds(Lnet/minecraft/world/level/block/state/BlockState;)I"))
	private int createbigcannons$checkBurnOut$getBurnOdds(FireBlock instance, BlockState state, Operation<Integer> original,
														  @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
		if (state.getBlock() instanceof CBCDynamicFlammableBlock cbcFlammable)
			return cbcFlammable.getFlammability(state, level, pos);
		return original.call(instance, state);
	}

	@WrapOperation(method = "getIgniteOdds(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)I",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;getIgniteOdds(Lnet/minecraft/world/level/block/state/BlockState;)I"))
	private int createbigcannons$getIgniteOdds(FireBlock instance, BlockState state, Operation<Integer> original,
											   @Local(argsOnly = true) LevelReader level, @Local(argsOnly = true) BlockPos pos,
											   @Local Direction direction) {
		if (state.getBlock() instanceof CBCDynamicFlammableBlock cbcFlammable)
			return cbcFlammable.getFireSpreadSpeed(state, level, pos.relative(direction));
		return original.call(instance, state);
	}

	@WrapOperation(method = "getStateForPlacement(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;canBurn(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
	private boolean createbigcannons$getStateForPlacement$below(FireBlock instance, BlockState state, Operation<Boolean> original,
																@Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
		if (state.getBlock() instanceof CBCDynamicFlammableBlock cbcFlammable)
			return cbcFlammable.getFireSpreadSpeed(state, level, pos.below()) > 0;
		return original.call(instance, state);
	}

	@WrapOperation(method = "getStateForPlacement(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;canBurn(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1))
	private boolean createbigcannons$getStateForPlacement$sides(FireBlock instance, BlockState state, Operation<Boolean> original,
																@Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos,
																@Local Direction direction) {
		if (state.getBlock() instanceof CBCDynamicFlammableBlock cbcFlammable)
			return cbcFlammable.getFireSpreadSpeed(state, level, pos.relative(direction)) > 0;
		return original.call(instance, state);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;canBurn(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean createbigcannons$tick(FireBlock instance, BlockState state, Operation<Boolean> original,
										  @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) BlockPos pos) {
		if (state.getBlock() instanceof CBCDynamicFlammableBlock cbcFlammable)
			return cbcFlammable.getFireSpreadSpeed(state, level, pos.below()) > 0;
		return original.call(instance, state);
	}

	@WrapOperation(method = "isValidFireLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;canBurn(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean createbigcannons$isValidFireLocation(FireBlock instance, BlockState state, Operation<Boolean> original,
														 @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos,
														 @Local Direction direction) {
		if (state.getBlock() instanceof CBCDynamicFlammableBlock cbcFlammable)
			return cbcFlammable.getFireSpreadSpeed(state, level, pos.relative(direction)) > 0;
		return original.call(instance, state);
	}

}
