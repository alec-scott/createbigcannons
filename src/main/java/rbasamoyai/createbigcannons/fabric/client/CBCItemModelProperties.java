package rbasamoyai.createbigcannons.fabric.client;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.multiloader.IndexPlatform;

/**
 * Exposes the numeric item properties registered through {@link IndexPlatform#registerClampedItemProperty} to item
 * model definitions as {@code range_dispatch} properties, e.g. {@code "property": "createbigcannons:fuze_state"}.
 *
 * <p>PORT-NOTE(mc26.1): replaces {@code ItemProperties.register} and the {@code overrides} predicates of the old item
 * models. The functions are looked up when the property is read, so registration order with
 * {@code CBCClientCommon.onClientSetup} doesn't matter.</p>
 */
public record CBCItemModelProperties(Identifier id) implements RangeSelectItemModelProperty {

	private static final String[] IDS = { "formed", "big_cartridge_filled", "fuze_state" };

	public static void register() {
		for (String path : IDS) {
			Identifier id = CreateBigCannons.resource(path);
			RangeSelectItemModelProperties.ID_MAPPER.put(id, MapCodec.unit(new CBCItemModelProperties(id)));
		}
	}

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		IndexPlatform.ItemPropertyFunction func = IndexPlatform.ITEM_PROPERTIES.get(this.id);
		return func == null ? 0 : func.call(stack, level, owner == null ? null : owner.asLivingEntity(), seed);
	}

	@Override
	public MapCodec<? extends RangeSelectItemModelProperty> type() {
		return MapCodec.unit(this);
	}

}
