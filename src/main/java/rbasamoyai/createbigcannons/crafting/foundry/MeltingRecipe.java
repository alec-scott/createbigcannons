package rbasamoyai.createbigcannons.crafting.foundry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.zurrtum.create.content.processing.basin.BasinInput;
import com.zurrtum.create.content.processing.basin.BasinRecipe;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import com.zurrtum.create.foundation.recipe.TimedRecipe;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import rbasamoyai.createbigcannons.index.CBCRecipeTypes;

/**
 * Melting in the basin foundry; same JSON format as Create's mixing.
 *
 * <p>PORT-NOTE(fabric): upstream extends Create's {@code BasinRecipe} class with {@code ProcessingRecipeParams}. Create Fly
 * models basin recipes as records implementing its {@code BasinRecipe} interface, so this is Create Fly's
 * {@code MixingRecipe} with CBC's type and serializer.</p>
 */
public record MeltingRecipe(int time, List<ProcessingOutput> results, List<FluidStack> fluidResults, HeatCondition heat,
                           List<FluidIngredient> fluidIngredients,
                           List<SizedIngredient> ingredients) implements BasinRecipe, TimedRecipe {
    public static final MapCodec<MeltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec((Instance<MeltingRecipe> instance) -> instance.group(
        Codec.INT.optionalFieldOf("processing_time", 100).forGetter(MeltingRecipe::time),
        ProcessingOutput.CODEC.listOf(1, 4).optionalFieldOf("results", List.of()).forGetter(MeltingRecipe::results),
        FluidStack.CODEC.listOf(1, 2).optionalFieldOf("fluid_results", List.of()).forGetter(MeltingRecipe::fluidResults),
        HeatCondition.CODEC.optionalFieldOf("heat_requirement", HeatCondition.NONE).forGetter(MeltingRecipe::heat),
        FluidIngredient.CODEC.listOf(1, 2).optionalFieldOf("fluid_ingredients", List.of())
            .forGetter(MeltingRecipe::fluidIngredients),
        SizedIngredient.LIST_CODEC.optionalFieldOf("ingredients", List.of()).forGetter(MeltingRecipe::ingredients)
    ).apply(instance, MeltingRecipe::new)).validate(recipe -> {
        if (recipe.results.isEmpty() && recipe.fluidResults.isEmpty()) {
            return DataResult.error(() -> "MeltingRecipe must have a result or a fluid result");
        }
        if (recipe.fluidIngredients.isEmpty() && recipe.ingredients.isEmpty()) {
            return DataResult.error(() -> "MeltingRecipe must have a ingredient or a fluid ingredient");
        }
        if (recipe.ingredients.size() > 9) {
            return DataResult.error(() -> "Ingredients type is too many: " + recipe.ingredients.size() + ", expected range [0-9]");
        }
        return DataResult.success(recipe);
    });
    private static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        MeltingRecipe::time,
        ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
        MeltingRecipe::results,
        FluidStack.PACKET_CODEC.apply(ByteBufCodecs.list()),
        MeltingRecipe::fluidResults,
        HeatCondition.PACKET_CODEC,
        MeltingRecipe::heat,
        FluidIngredient.PACKET_CODEC.apply(ByteBufCodecs.list()),
        MeltingRecipe::fluidIngredients,
        SizedIngredient.PACKET_CODEC.apply(ByteBufCodecs.list()),
        MeltingRecipe::ingredients,
        MeltingRecipe::new
    );
    public static final RecipeSerializer<MeltingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public int getIngredientSize() {
        return fluidIngredients.size() + ingredients().size();
    }

    @Override
    public boolean matches(BasinInput input, Level world) {
        if (!heat.testBlazeBurner(input.heat())) {
            return false;
        }
        ServerFilteringBehaviour filter = input.filter();
        if (filter == null) {
            return false;
        }
        if (results.isEmpty()) {
            if (!filter.test(fluidResults.getFirst())) {
                return false;
            }
        } else if (!filter.test(results.getFirst().create())) {
            return false;
        }
        List<ItemStack> outputs = BasinRecipe.tryCraft(input, ingredients);
        if (outputs == null) {
            return false;
        }
        if (!BasinRecipe.matchFluidIngredient(input, fluidIngredients)) {
            return false;
        }
        ProcessingOutput.rollOutput(input.random(), results, outputs::add);
        return input.acceptOutputs(outputs, fluidResults, true);
    }

    @Override
    public boolean apply(BasinInput input) {
        if (!heat.testBlazeBurner(input.heat())) {
            return false;
        }
        Deque<Runnable> changes = new ArrayDeque<>();
        List<ItemStack> outputs = BasinRecipe.prepareCraft(input, ingredients, changes);
        if (outputs == null) {
            return false;
        }
        if (!BasinRecipe.prepareFluidCraft(input, fluidIngredients, changes)) {
            return false;
        }
        ProcessingOutput.rollOutput(input.random(), results, outputs::add);
        if (!input.acceptOutputs(outputs, fluidResults, true)) {
            return false;
        }
        changes.forEach(Runnable::run);
        return input.acceptOutputs(outputs, fluidResults, false);
    }

    @Override
    public RecipeSerializer<MeltingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<MeltingRecipe> getType() {
        return CBCRecipeTypes.MELTING.getType();
    }
}
