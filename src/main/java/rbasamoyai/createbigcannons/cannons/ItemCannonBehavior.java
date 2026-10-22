package rbasamoyai.createbigcannons.cannons;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemCannonBehavior extends CannonBehavior {

	public static final BehaviourType<ItemCannonBehavior> TYPE = new BehaviourType<>();

	private ItemStack containedStack = ItemStack.EMPTY;

	public ItemCannonBehavior(SmartBlockEntity te) {
		super(te);
	}

	public boolean tryLoadingItem(ItemStack stack) {
		if (!this.canLoadItem(stack)) return false;
		this.containedStack = stack;
		return true;
	}

	public boolean canLoadItem(ItemStack stack) {
		return this.containedStack.isEmpty();
	}

	public void removeItem() {
		this.containedStack = ItemStack.EMPTY;
	}

	public ItemStack getItem() {
		return this.containedStack;
	}

	@Override
	public void write(ValueOutput nbt, boolean spawnPacket) {
		nbt.store("ContainedStack", ItemStack.OPTIONAL_CODEC, this.containedStack);
		super.write(nbt, spawnPacket);
	}

    @Override
    public void writeSafe(ValueOutput nbt) {
        super.write(nbt, false);
    }

	@Override
	public void read(ValueInput nbt, boolean clientPacket) {
		this.containedStack = nbt.read("ContainedStack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
		super.read(nbt, clientPacket);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

}
