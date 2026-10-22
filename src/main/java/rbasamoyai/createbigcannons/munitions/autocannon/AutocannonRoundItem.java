package rbasamoyai.createbigcannons.munitions.autocannon;

import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.zurrtum.create.client.foundation.utility.CreateLang;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;

public abstract class AutocannonRoundItem extends Item {

	protected AutocannonRoundItem(Properties properties) {
		super(properties);
	}

	public abstract AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level);

	public abstract EntityType<?> getEntityType(ItemStack stack);

	@NotNull
	public abstract AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack itemStack);

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCItems.AUTOCANNON_CARTRIDGE.asStack();
		AutocannonCartridgeItem.writeProjectile(this.getDefaultInstance(), stack);
		return stack;
	}

	// PORT-NOTE(mc26.1): List-based tooltip, called from the appendHoverText(..., Consumer) override below.
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		if (stack.getOrDefault(CBCDataComponents.AUTOCANNON_TRACER, false)) {
			CreateLang.builder("tooltip").translate(CreateBigCannons.MOD_ID + ".tracer").addTo(tooltipComponents);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		List<Component> tooltip = new ArrayList<>();
		this.appendHoverText(stack, context, tooltip, flag);
		tooltip.forEach(builder);
	}

}
