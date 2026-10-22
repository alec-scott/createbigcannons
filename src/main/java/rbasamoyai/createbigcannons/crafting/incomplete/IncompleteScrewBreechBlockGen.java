package rbasamoyai.createbigcannons.crafting.incomplete;

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

public class IncompleteScrewBreechBlockGen extends SpecialBlockStateGen {

	public static IncompleteScrewBreechBlockGen create(String pathAndMaterial) { return new IncompleteScrewBreechBlockGen(pathAndMaterial); }

	protected final String pathAndMaterial;

	protected IncompleteScrewBreechBlockGen(String pathAndMaterial) {
		this.pathAndMaterial = pathAndMaterial;
	}

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> context, RegistrateBlockstateProvider provider, BlockState state) {
        int stage = state.getValue(IncompleteScrewBreechBlock.STAGE_2);
        String suf = stage == 0 ? "" : "_axis";
        Identifier baseLoc = CreateBigCannons.resource("block/screw_breech" + suf);

        Identifier topLoc = CreateBigCannons.resource("block/" + this.pathAndMaterial + "_screw_breech_top");
        Identifier bottomLoc = CreateBigCannons.resource("block/" + this.pathAndMaterial + "_screw_breech_bottom");
        Identifier sideLoc = CreateBigCannons.resource("block/" + this.pathAndMaterial + "_screw_breech_side");
        return provider.models().withExistingParent(context.getName() + suf, baseLoc)
            .texture("side", sideLoc)
            .texture("top", topLoc)
            .texture("bottom", bottomLoc)
            .texture("particle", topLoc);
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
