package rbasamoyai.createbigcannons.crafting.munition_assembly;

import com.google.common.collect.Lists;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCRecipeTypes;
import rbasamoyai.createbigcannons.munitions.FuzedItemMunition;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

public class MunitionFuzingRecipe extends CustomRecipe {

	public MunitionFuzingRecipe() {
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack round = ItemStack.EMPTY;
		ItemStack fuze = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.getItem() instanceof AutocannonCartridgeItem) {
				if (!round.isEmpty()) return false;
				stack = AutocannonCartridgeItem.getProjectileStack(stack);
			}
			if (stack.getItem() instanceof FuzedItemMunition) {
				if (!round.isEmpty() || stack.get(CBCDataComponents.FUZE) != null) return false; // todo: playtest 1.21
				round = stack;
			} else if (stack.getItem() instanceof FuzeItem) {
				if (!fuze.isEmpty()) return false;
				fuze = stack;
			} else {
				return false;
			}
		}

		return !round.isEmpty() && !fuze.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		ItemStack round = ItemStack.EMPTY;
		ItemStack fuze = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() instanceof FuzedItemMunition || stack.getItem() instanceof AutocannonCartridgeItem) {
				if (!round.isEmpty()) return ItemStack.EMPTY;
				round = stack;
			} else if (stack.getItem() instanceof FuzeItem) {
				if (!fuze.isEmpty()) return ItemStack.EMPTY;
				fuze = stack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		if (round.isEmpty() || fuze.isEmpty()) return ItemStack.EMPTY;
		ItemStack result = round.copy();
		result.setCount(1);
		ItemStack fuzeCopy = fuze.copy();
		fuzeCopy.setCount(1);
		if (result.getItem() instanceof FuzedItemMunition) {
            result.set(CBCDataComponents.FUZE, ItemContainerContents.fromItems(Lists.newArrayList(fuzeCopy)));
		} else if (result.getItem() instanceof AutocannonCartridgeItem) {
            ItemContainerContents items = result.getOrDefault(CBCDataComponents.PROJECTILE, ItemContainerContents.EMPTY);
            ItemStack projectile = items.copyOne(); // This should not be EMPTY
            if (!projectile.isEmpty()) {
                projectile.set(CBCDataComponents.FUZE, ItemContainerContents.fromItems(Lists.newArrayList(fuzeCopy)));
                result.set(CBCDataComponents.PROJECTILE, ItemContainerContents.fromItems(Lists.newArrayList(projectile)));
            }
		}
		return result;
	}

	@Override public RecipeSerializer<? extends CustomRecipe> getSerializer() { return CBCRecipeTypes.MUNITION_FUZING.getSerializer(); }
}
