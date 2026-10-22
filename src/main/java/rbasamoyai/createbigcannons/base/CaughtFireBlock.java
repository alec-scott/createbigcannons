package rbasamoyai.createbigcannons.base;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** PORT-NOTE(fabric): NeoForge's {@code IBlockExtension#onCaughtFire}, called from {@code FireBlockMixin}. */
public interface CaughtFireBlock {

	void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter);

}
