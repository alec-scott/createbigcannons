package rbasamoyai.createbigcannons.munitions.config;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class ItemPropertiesTypeHandler<PROPERTIES> extends PropertiesTypeHandler<Item, PROPERTIES> {

	@NotNull public final PROPERTIES getPropertiesOf(ItemStack itemStack) { return this.getPropertiesOf(itemStack.getItem()); }

}
