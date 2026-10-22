package rbasamoyai.createbigcannons.base;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.crafting.BlockRecipeSerializer;
import rbasamoyai.createbigcannons.crafting.BlockRecipeType;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;

/**
 * CBC's own registries.
 *
 * <p>PORT-NOTE(fabric): NeoForge created these in {@code NewRegistryEvent}; Fabric registries are created eagerly with
 * {@link FabricRegistryBuilder} (same default keys, synced to clients). Call {@link #init()} before registering into them.</p>
 */
public class CBCRegistries {

	public static final ResourceKey<Registry<BlockRecipeSerializer<?>>> BLOCK_RECIPE_SERIALIZERS = key("block_recipe_serializers");
	public static final ResourceKey<Registry<BlockRecipeType<?>>> BLOCK_RECIPE_TYPES = key("block_recipe_types");
	public static final ResourceKey<Registry<CannonCastShape>> CANNON_CAST_SHAPES = key("cannon_cast_shapes");

	private static final Registry<BlockRecipeSerializer<?>> BLOCK_RECIPE_SERIALIZERS_REGISTRY =
		FabricRegistryBuilder.createDefaulted(BLOCK_RECIPE_SERIALIZERS, CreateBigCannons.resource("cannon_casting"))
			.attribute(RegistryAttribute.SYNCED).buildAndRegister();
	private static final Registry<BlockRecipeType<?>> BLOCK_RECIPE_TYPES_REGISTRY =
		FabricRegistryBuilder.createDefaulted(BLOCK_RECIPE_TYPES, CreateBigCannons.resource("cannon_casting"))
			.attribute(RegistryAttribute.SYNCED).buildAndRegister();
	private static final Registry<CannonCastShape> CANNON_CAST_SHAPES_REGISTRY =
		FabricRegistryBuilder.createDefaulted(CANNON_CAST_SHAPES, CreateBigCannons.resource("very_small"))
			.attribute(RegistryAttribute.SYNCED).buildAndRegister();

	private static <T> ResourceKey<Registry<T>> key(String id) { return ResourceKey.createRegistryKey(CreateBigCannons.resource(id)); }

	/** Creates the registries (class init) and fills them. */
	public static void init() {
		BlockRecipeSerializer.register();
		BlockRecipeType.register();
		CannonCastShape.register();
	}

	public static Registry<BlockRecipeSerializer<?>> blockRecipeSerializers() { return BLOCK_RECIPE_SERIALIZERS_REGISTRY; }

	public static Registry<BlockRecipeType<?>> blockRecipeTypes() { return BLOCK_RECIPE_TYPES_REGISTRY; }

	public static Registry<CannonCastShape> cannonCastShapes() { return CANNON_CAST_SHAPES_REGISTRY; }

}
