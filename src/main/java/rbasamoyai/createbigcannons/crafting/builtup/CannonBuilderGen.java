package rbasamoyai.createbigcannons.crafting.builtup;

import rbasamoyai.createbigcannons.port.create.foundation.data.SpecialBlockStateGen;
import rbasamoyai.createbigcannons.port.registrate.providers.DataGenContext;
import rbasamoyai.createbigcannons.port.registrate.providers.RegistrateBlockstateProvider;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.port.porting_lib.models.generators.ModelFile;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.crafting.boring.CannonDrillBlock;

public class CannonBuilderGen extends SpecialBlockStateGen {

	public static CannonBuilderGen create() { return new CannonBuilderGen(); }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        boolean axisAlongFirst = state.getValue(CannonBuilderBlock.AXIS_ALONG_FIRST_COORDINATE);
        boolean rotated = state.getValue(CannonBuilderBlock.FACING).getAxis() == Direction.Axis.X ^ axisAlongFirst;

        String variant = state.getValue(CannonBuilderBlock.STATE).getSerializedName() + (rotated ? "_rotated" : "");
        return prov.models().getExistingFile(CreateBigCannons.resource("block/cannon_builder/" + variant));
    }

	@Override
	protected int getXRotation(BlockState state) {
		Direction facing = state.getValue(CannonDrillBlock.FACING);
		return facing.getAxis().isVertical() ? facing == Direction.DOWN ? 180 : 0 : 90;
	}

	@Override
	protected int getYRotation(BlockState state) {
		Direction facing = state.getValue(CannonDrillBlock.FACING);
		return facing.getAxis().isVertical() ? 0 : horizontalAngle(facing) + 180;
	}

}
