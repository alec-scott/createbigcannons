package rbasamoyai.createbigcannons.crafting.munition_assembly;

import java.util.List;

import com.zurrtum.create.content.kinetics.deployer.ItemApplicationInput;
import com.zurrtum.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

/**
 * Base for CBC's deployer recipes, which are built on the fly from the deployer's held item and the item it works on.
 *
 * <p>PORT-NOTE(fabric): NeoForge Create applied any {@code Recipe} found by its deployer recipe event; Create Fly's belt
 * deployer only applies {@link ItemApplicationRecipe}s, so these implement it. They are supplied by
 * {@code DeployerBlockEntityMixin}, never loaded from data, so {@link #target()} and {@link #ingredient()} are only
 * placeholders; matching uses {@link #matchesInput}.</p>
 */
public abstract class CBCDeployerRecipe implements ItemApplicationRecipe {

	public abstract boolean matchesInput(RecipeInput input, Level level);

	public abstract ItemStack getResultItem();

	@Override
	public boolean matches(ItemApplicationInput input, Level level) {
		return this.matchesInput(input, level);
	}

	@Override
	public List<ItemStack> assemble(ItemApplicationInput input, RandomSource random) {
		return List.of(this.getResultItem());
	}

	@Override public List<ProcessingOutput> results() { return List.of(); }
	@Override public boolean keepHeldItem() { return false; }
	@Override public Ingredient target() { return Ingredient.of(Items.BARRIER); }
	@Override public Ingredient ingredient() { return Ingredient.of(Items.BARRIER); }

}
