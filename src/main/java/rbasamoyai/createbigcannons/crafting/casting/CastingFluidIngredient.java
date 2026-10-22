package rbasamoyai.createbigcannons.crafting.casting;

import java.util.function.Predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

/**
 * A fluid or fluid tag, without an amount.
 *
 * <p>PORT-NOTE(fabric): replaces NeoForge's {@code FluidIngredient} in {@link CannonCastingRecipe}, reading the same
 * {@code {"fluid": ...}} / {@code {"tag": ...}} JSON. Create Fly's fluid ingredients always carry an amount.</p>
 */
public record CastingFluidIngredient(Either<Fluid, TagKey<Fluid>> value) implements Predicate<FluidStack> {

	public static final Codec<CastingFluidIngredient> CODEC = Codec.mapEither(
		BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid"),
		TagKey.codec(Registries.FLUID).fieldOf("tag")
	).codec().xmap(CastingFluidIngredient::new, CastingFluidIngredient::value);

	public static final StreamCodec<RegistryFriendlyByteBuf, CastingFluidIngredient> STREAM_CODEC = ByteBufCodecs.either(
		ByteBufCodecs.registry(Registries.FLUID),
		TagKey.streamCodec(Registries.FLUID)
	).map(CastingFluidIngredient::new, CastingFluidIngredient::value);

	@Override
	public boolean test(FluidStack stack) {
		if (stack.isEmpty())
			return false;
		Fluid fluid = stack.getFluid();
		return this.value.map(f -> f.isSame(fluid), tag -> fluid.builtInRegistryHolder().is(tag));
	}

}
