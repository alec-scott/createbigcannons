package rbasamoyai.createbigcannons.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Flammability that depends on the block entity.
 *
 * <p>PORT-NOTE(fabric): NeoForge's position-aware {@code getFlammability}/{@code getFireSpreadSpeed}; Fabric's
 * {@code FlammableBlockRegistry} only takes fixed values per block. Read by {@code FireBlockMixin}.</p>
 */
public interface CBCDynamicFlammableBlock {

	int getFlammability(BlockState state, BlockGetter level, BlockPos pos);

	int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos);

}
