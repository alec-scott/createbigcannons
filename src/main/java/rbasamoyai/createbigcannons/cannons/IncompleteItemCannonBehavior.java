package rbasamoyai.createbigcannons.cannons;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.world.item.ItemStack;

public class IncompleteItemCannonBehavior extends ItemCannonBehavior {

	public IncompleteItemCannonBehavior(SmartBlockEntity te) {
		super(te);
	}

	@Override
	public boolean canLoadItem(ItemStack stack) {
		return false;
	}

}
