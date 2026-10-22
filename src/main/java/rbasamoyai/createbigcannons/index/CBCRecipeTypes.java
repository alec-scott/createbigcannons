package rbasamoyai.createbigcannons.index;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import java.util.Locale;
import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullSupplier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.crafting.foundry.MeltingRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.AutocannonAmmoContainerFillingDeployerRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.BigCartridgeFillingDeployerRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.BigCartridgeFillingRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.CartridgeAssemblyDeployerRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.CartridgeAssemblyRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.FuzeRemovalRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.MunitionFuzingDeployerRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.MunitionFuzingRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.TracerApplicationDeployerRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.TracerApplicationRecipe;
import rbasamoyai.createbigcannons.crafting.munition_assembly.TracerRemovalRecipe;
import rbasamoyai.createbigcannons.multiloader.IndexPlatform;

/**
 * PORT-NOTE(fabric): no longer implements Create's {@code IRecipeTypeInfo} (not in Create Fly); 26.1's {@link RecipeSerializer}
 * is a record of codecs. Ids use plain lower-casing (Create Fly's {@code CreateLang} is client-only).
 */
public enum CBCRecipeTypes {

	MELTING(() -> MeltingRecipe.SERIALIZER),
	MUNITION_FUZING(noSerializer(MunitionFuzingRecipe::new)),
	CARTRIDGE_ASSEMBLY(noSerializer(CartridgeAssemblyRecipe::new)),
	BIG_CARTRIDGE_FILLING(noSerializer(BigCartridgeFillingRecipe::new)),
	BIG_CARTRIDGE_FILLING_DEPLOYER(noSerializer(BigCartridgeFillingDeployerRecipe::new)),
	MUNITION_FUZING_DEPLOYER(noSerializer(MunitionFuzingDeployerRecipe::new)),
	CARTRIDGE_ASSEMBLY_DEPLOYER(noSerializer(CartridgeAssemblyDeployerRecipe::new)),
	TRACER_APPLICATION(noSerializer(TracerApplicationRecipe::new)),
	TRACER_APPLICATION_DEPLOYER(noSerializer(TracerApplicationDeployerRecipe::new)),
	AUTOCANNON_AMMO_CONTAINER_FILLING_DEPLOYER(noSerializer(AutocannonAmmoContainerFillingDeployerRecipe::new)),
	FUZE_REMOVAL(noSerializer(FuzeRemovalRecipe::new)),
	TRACER_REMOVAL(noSerializer(TracerRemovalRecipe::new));

	private final Identifier id;
	private final Supplier<RecipeSerializer<?>> serializerObject;
	@Nullable
	private final RecipeType<?> typeObject;
	private final NonNullSupplier<RecipeType<?>> type;

	CBCRecipeTypes(NonNullSupplier<RecipeSerializer<?>> serializerSupplier, NonNullSupplier<RecipeType<?>> typeSupplier, boolean registerType) {
		String name = name().toLowerCase(Locale.ROOT);
		id = CreateBigCannons.resource(name);
		serializerObject = IndexPlatform.registerRecipeSerializer(this.id, serializerSupplier);
		if (registerType) {
			typeObject = typeSupplier.get();
			IndexPlatform.registerRecipeType(this.id, typeSupplier);
			type = typeSupplier;
		} else {
			typeObject = null;
			type = typeSupplier;
		}
	}

	CBCRecipeTypes(NonNullSupplier<RecipeSerializer<?>> serializerSupplier) {
		String name = name().toLowerCase(Locale.ROOT);
		id = CreateBigCannons.resource(name);
		serializerObject = IndexPlatform.registerRecipeSerializer(this.id, serializerSupplier);
		typeObject = simpleType(id);
		type = () -> typeObject;
		IndexPlatform.registerRecipeType(this.id, this.type);
	}

	public static <T extends Recipe<?>> RecipeType<T> simpleType(Identifier id) {
		String stringId = id.toString();
		return new RecipeType<>() {
			@Override
			public String toString() {
				return stringId;
			}
		};
	}

	public Identifier getId() {
		return this.id;
	}

	@SuppressWarnings("unchecked")
	public <T extends RecipeSerializer<?>> T getSerializer() {
		return (T) this.serializerObject.get();
	}

	@SuppressWarnings("unchecked")
	public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
		return (RecipeType<R>) this.type.get();
	}

	public static void register() {
	}

	private static <T extends Recipe<?>> NonNullSupplier<RecipeSerializer<?>> noSerializer(Supplier<T> prov) {
		return () -> new RecipeSerializer<>(MapCodec.unit(prov), StreamCodec.of((buf, recipe) -> {}, buf -> prov.get()));
	}

}
