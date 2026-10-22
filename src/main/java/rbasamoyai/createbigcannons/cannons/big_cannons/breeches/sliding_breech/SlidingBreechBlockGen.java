package rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech;

import com.zurrtum.create.content.kinetics.base.DirectionalAxisKineticBlock;
import rbasamoyai.createbigcannons.port.create.foundation.data.SpecialBlockStateGen;
import rbasamoyai.createbigcannons.port.registrate.providers.DataGenContext;
import rbasamoyai.createbigcannons.port.registrate.providers.RegistrateBlockstateProvider;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.createbigcannons.port.porting_lib.models.generators.ModelFile;
import rbasamoyai.createbigcannons.CreateBigCannons;

public class SlidingBreechBlockGen extends SpecialBlockStateGen {

	public static SlidingBreechBlockGen create(String pathAndMaterial) { return new SlidingBreechBlockGen(pathAndMaterial); }

	protected final String pathAndMaterial;

	public SlidingBreechBlockGen(String pathAndMaterial) {
		this.pathAndMaterial = pathAndMaterial;
	}

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> context, RegistrateBlockstateProvider provider, BlockState state) {
        boolean axisAlongFirst = state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
        boolean rotated = (state.getValue(BlockStateProperties.FACING).getAxis() == Direction.Axis.X) == axisAlongFirst;
        String suf = rotated ? "_rotated" : "";
        Identifier baseLoc = CreateBigCannons.resource("block/sliding_breech" + suf);

        Identifier holeLoc = CreateBigCannons.resource("block/" + this.pathAndMaterial + "_sliding_breech_hole");
        Identifier sideLoc = CreateBigCannons.resource("block/" + this.pathAndMaterial + "_sliding_breech_side");
        Identifier sideHoleLoc = CreateBigCannons.resource("block/" + this.pathAndMaterial + "_sliding_breech_side_hole");
        Identifier insideLoc = CreateBigCannons.resource("block/" + this.pathAndMaterial + "_sliding_breech_inside");
        return provider.models().withExistingParent(context.getName() + suf, baseLoc)
            .texture("hole", holeLoc)
            .texture("side", sideLoc)
            .texture("side_hole", sideHoleLoc)
            .texture("inside", insideLoc);
    }

	@Override
	protected int getXRotation(BlockState state) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		return facing.getAxis().isVertical() ? facing == Direction.DOWN ? 180 : 0 : 90;
	}

	@Override
	protected int getYRotation(BlockState state) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		return facing.getAxis().isVertical() ? 0 : this.horizontalAngle(facing) + 180;
	}

}
