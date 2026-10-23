package rbasamoyai.createbigcannons.munitions.big_cannon.traffic_cone;

import net.minecraft.world.InteractionResult;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class TrafficConeBlockItem extends BlockItem {

	public TrafficConeBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
		ItemStack itemStack = player.getItemInHand(usedHand);
		ItemStack itemStack2 = player.getItemBySlot(EquipmentSlot.HEAD);
		if (itemStack2.isEmpty()) {
			ItemStack copy = itemStack.copy();
			copy.setCount(1);
			player.setItemSlot(EquipmentSlot.HEAD, copy);
			if (!level.isClientSide()) {
				player.awardStat(Stats.ITEM_USED.get(this));
			}
			itemStack.shrink(1);
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.FAIL;
		}
	}

	// PORT-NOTE(mc26.1): Equipable is gone; the head slot comes from the EQUIPPABLE component set in CBCBlocks.

}
