package rbasamoyai.createbigcannons.munitions.config;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockPropertiesTypeHandler<PROPERTIES> extends PropertiesTypeHandler<Block, PROPERTIES> {

	@NotNull public final PROPERTIES getPropertiesOf(BlockState blockState) { return this.getPropertiesOf(blockState.getBlock()); }

}
