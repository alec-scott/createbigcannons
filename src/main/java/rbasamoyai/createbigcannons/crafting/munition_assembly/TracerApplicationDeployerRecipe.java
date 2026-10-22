package rbasamoyai.createbigcannons.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.index.CBCRecipeTypes;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

public class TracerApplicationDeployerRecipe extends CBCDeployerRecipe {

	private final ItemStack munition;
	private final ItemStack fuze;

	public TracerApplicationDeployerRecipe() {
		this.munition = ItemStack.EMPTY;
		this.fuze = ItemStack.EMPTY;
	}
	public TracerApplicationDeployerRecipe(ItemStack munition, ItemStack fuze) {
		this.munition = munition.copy();
		this.fuze = fuze.copy();
	}

	@Override
	public boolean matchesInput(RecipeInput input, Level level) {
		if (!CBCItems.TRACER_TIP.isIn(this.fuze)) return false;
		if (this.munition.getItem() instanceof AutocannonRoundItem) return !this.munition.getOrDefault(CBCDataComponents.AUTOCANNON_TRACER, false);
		if (this.munition.getItem() instanceof AutocannonAmmoItem item) return !item.isTracer(this.munition);
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		ItemStack result = this.munition.copy();
		result.setCount(1);
		if (result.getItem() instanceof AutocannonRoundItem) {
			result.set(CBCDataComponents.AUTOCANNON_TRACER, true);
		} else if (result.getItem() instanceof AutocannonAmmoItem item) {
			item.setTracer(result, true);
		}
		return result;
	}

	@Override public RecipeSerializer<? extends CBCDeployerRecipe> getSerializer() { return CBCRecipeTypes.TRACER_APPLICATION_DEPLOYER.getSerializer(); }
	@Override public RecipeType<? extends CBCDeployerRecipe> getType() { return CBCRecipeTypes.TRACER_APPLICATION_DEPLOYER.getType(); }

}
