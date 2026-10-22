package rbasamoyai.createbigcannons.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.CBCTags;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.index.CBCRecipeTypes;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.BigCartridgeBlockItem;

public class BigCartridgeFillingDeployerRecipe extends CBCDeployerRecipe {

	private final int startPower;
	private final int resultPower;

	public BigCartridgeFillingDeployerRecipe() {
		this.startPower = 0;
		this.resultPower = 0;
	}
	public BigCartridgeFillingDeployerRecipe(int startPower, int resultPower) {
		this.startPower = startPower;
		this.resultPower = resultPower;
	}

	@Override
	public boolean matchesInput(RecipeInput input, Level level) {
		ItemStack cartridge = input.getItem(0);
		return CBCBlocks.BIG_CARTRIDGE.isIn(cartridge) && BigCartridgeBlockItem.getPower(cartridge) == this.startPower
			&& input.getItem(1).is(CBCTags.CBCItemTags.NITROPOWDER);
	}
	@Override public ItemStack getResultItem() { return BigCartridgeBlockItem.getWithPower(this.resultPower); }

	@Override public RecipeSerializer<? extends CBCDeployerRecipe> getSerializer() { return CBCRecipeTypes.BIG_CARTRIDGE_FILLING_DEPLOYER.getSerializer(); }
	@Override public RecipeType<? extends CBCDeployerRecipe> getType() { return CBCRecipeTypes.BIG_CARTRIDGE_FILLING_DEPLOYER.getType(); }

}
