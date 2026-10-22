package rbasamoyai.createbigcannons.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block (with a block entity) that reacts to being replaced while its block entity still exists.
 *
 * <p>PORT-NOTE(mc26.1): {@code Block#onRemove} is gone; {@code affectNeighborsAfterRemoval} runs after the block entity
 * has been removed. {@code LevelChunkMixin} calls this just before the block entity's
 * {@code preRemoveSideEffects}, when the new state is already in the chunk. Server side only.</p>
 */
public interface CBCRemovalListener {

	void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving);

}
