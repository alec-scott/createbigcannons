package rbasamoyai.createbigcannons.munitions.big_cannon;

import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createbigcannons.index.CBCDataComponents;

public class ProjectileBlockItem extends BlockItem {

	public ProjectileBlockItem(Block block, Properties properties) {
		super(block, properties.component(CBCDataComponents.TRACER, ItemContainerContents.EMPTY));
	}

	// PORT-NOTE(mc26.1): List-based tooltip, called from the appendHoverText(..., Consumer) override below.
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        ItemContainerContents items = stack.getOrDefault(CBCDataComponents.TRACER, ItemContainerContents.EMPTY);
		ItemStack tracer = items.copyOne();
		if (!tracer.isEmpty())
			tooltip.add(Component.translatable("tooltip.createbigcannons.tracer"));
	}
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		List<Component> tooltip = new ArrayList<>();
		this.appendHoverText(stack, context, tooltip, flag);
		tooltip.forEach(builder);
	}

}
