package rbasamoyai.createbigcannons.munitions.big_cannon.mortar_stone;

import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createbigcannons.base.CBCTooltip;

public class MortarStoneItem extends BlockItem {

    public MortarStoneItem(Block block, Properties properties) {
        super(block, properties);
    }

    // PORT-NOTE(mc26.1): List-based tooltip, called from the appendHoverText(..., Consumer) override below.
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        CBCTooltip.appendMortarStoneText(stack, ctx, tooltip, flag);
    }

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		List<Component> tooltip = new ArrayList<>();
		this.appendHoverText(stack, context, tooltip, flag);
		tooltip.forEach(builder);
	}

}
