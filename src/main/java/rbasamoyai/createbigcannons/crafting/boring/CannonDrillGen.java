package rbasamoyai.createbigcannons.crafting.boring;

import com.zurrtum.create.content.kinetics.base.DirectionalAxisKineticBlock;
import rbasamoyai.createbigcannons.port.create.foundation.data.SpecialBlockStateGen;
import rbasamoyai.createbigcannons.port.registrate.providers.DataGenContext;
import rbasamoyai.createbigcannons.port.registrate.providers.RegistrateBlockstateProvider;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.port.porting_lib.models.generators.ModelFile;
import rbasamoyai.createbigcannons.CreateBigCannons;

public class CannonDrillGen extends SpecialBlockStateGen {

	public static CannonDrillGen create() { return new CannonDrillGen(); }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> context, RegistrateBlockstateProvider provider, BlockState state) {
        boolean axisAlongFirst = state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
        boolean rotated = state.getValue(CannonDrillBlock.FACING).getAxis() == Direction.Axis.X ^ axisAlongFirst;
        String suf = state.getValue(CannonDrillBlock.STATE).getSerializedName() + (rotated ? "_rotated" : "");
        return provider.models().getExistingFile(CreateBigCannons.resource("block/cannon_drill/" + suf));
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
