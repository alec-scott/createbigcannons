package rbasamoyai.createbigcannons.multiloader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.JsonOps;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import com.zurrtum.create.foundation.fluid.FluidStackIngredient;
import com.zurrtum.create.foundation.fluid.FluidTagIngredient;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.particle.FluidParticleData;
import com.zurrtum.create.infrastructure.player.FakePlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.crafting.boring.AbstractCannonDrillBlockEntity;
import rbasamoyai.createbigcannons.crafting.boring.CannonDrillBlockEntity;
import rbasamoyai.createbigcannons.crafting.casting.AbstractCannonCastBlockEntity;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.AbstractFluidShellBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.EndFluidStack;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.FluidShellBlockEntity;
import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullFunction;
import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullSupplier;

/**
 * Loader-specific helpers.
 *
 * <p>PORT-NOTE(fabric): this was the NeoForge implementation; it now targets Fabric and Create Fly (cf. the 1.20.1
 * branch's {@code fabric/.../IndexPlatformImpl}). Fluid amounts are Fabric droplets (81000 per bucket), so
 * {@link #getFluidConversionFactor()} scales CBC's millibucket values by 81.</p>
 */
public class IndexPlatform {

	public static boolean isFakePlayer(Player player) {
		return player instanceof FakePlayer || player instanceof FakePlayerEntity;
	}

	public static AbstractCannonDrillBlockEntity makeDrill(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		return new CannonDrillBlockEntity(type, pos, state);
	}

	public static AbstractCannonCastBlockEntity makeCast(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		return new CannonCastBlockEntity(type, pos, state);
	}

	public static AbstractAutocannonBreechBlockEntity makeAutocannonBreech(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		return new AutocannonBreechBlockEntity(type, pos, state);
	}

	public static AbstractFluidShellBlockEntity makeFluidShellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		return new FluidShellBlockEntity(type, pos, state);
	}

	public static ParticleOptions createFluidDripParticle(EndFluidStack stack) {
		// Same particle as Create Fly's client-only FluidFX.getFluidParticle, built here so servers can send it.
		return FluidParticleData.particle(stack.fluid(), stack.components().asPatch());
	}

	public static void registerDeferredParticleType(String name, ParticleType<?> type) {
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, CreateBigCannons.resource(name), type);
	}

	public static void registerDeferredParticles() {
	}

	@Environment(EnvType.CLIENT)
	public static KeyMapping createSafeKeyMapping(String description, InputConstants.Type type, int keycode) {
		return new KeyMapping(description, type, keycode, CBCKeyCategory.CATEGORY);
	}

	/** Numeric item-model property, as {@code ItemPropertyFunction} was before 1.21.4. */
	@FunctionalInterface
	public interface ItemPropertyFunction {
		float call(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed);
	}

	/**
	 * Item properties by id; read by {@code CBCItemModelProperties}, which exposes them to item-model definitions as
	 * {@code range_dispatch} properties. PORT-NOTE(mc26.1): {@code ItemProperties.register} is gone (1.21.4 item models).
	 */
	public static final Map<Identifier, ItemPropertyFunction> ITEM_PROPERTIES = new LinkedHashMap<>();

	@Environment(EnvType.CLIENT)
	public static void registerClampedItemProperty(Item item, Identifier loc, ItemPropertyFunction func) {
		ItemPropertyFunction previous = ITEM_PROPERTIES.get(loc);
		ITEM_PROPERTIES.put(loc, (stack, level, entity, seed) -> stack.is(item) ? func.call(stack, level, entity, seed)
			: previous == null ? 0 : previous.call(stack, level, entity, seed));
	}

	@Environment(EnvType.CLIENT)
	public static void registerGenericClampedItemProperty(Identifier loc, ItemPropertyFunction func) {
		ITEM_PROPERTIES.put(loc, func);
	}

	public static Supplier<RecipeSerializer<?>> registerRecipeSerializer(Identifier id, NonNullSupplier<RecipeSerializer<?>> sup) {
		RecipeSerializer<?> serializer = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, sup.get());
		return () -> serializer;
	}

	public static void registerRecipeType(Identifier id, Supplier<RecipeType<?>> type) {
		Registry.register(BuiltInRegistries.RECIPE_TYPE, id, type.get());
	}

	public static float getFluidConversionFactor() {
		return 81;
	}

	public static FluidIngredient fluidIngredientFrom(Fluid fluid, int amount) {
		return new FluidStackIngredient(fluid, DataComponentPatch.EMPTY, amount);
	}

	public static FluidIngredient fluidIngredientFrom(TagKey<Fluid> fluid, int amount) {
		return new FluidTagIngredient(fluid, amount);
	}

	/** NeoForge let other mods cancel explosions here; Fabric has no such event, so nothing cancels them. */
	public static boolean onExplosionStart(Level level, Explosion explosion) {
		return false;
	}

	public static RegistryOps<JsonElement> makeRegistryOps(RegistryAccess access, SimpleJsonResourceReloadListener<?> listener) {
		// NeoForge used the listener's condition-aware ops; Fabric resource conditions are applied before parsing.
		// Without a registry access (client-side data), the built-in registries are enough.
		return RegistryOps.create(JsonOps.INSTANCE, access != null ? access : RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
	}

}
