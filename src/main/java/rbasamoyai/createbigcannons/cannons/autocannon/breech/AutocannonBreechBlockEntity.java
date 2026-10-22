package rbasamoyai.createbigcannons.cannons.autocannon.breech;

import com.zurrtum.create.AllClientHandle;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class AutocannonBreechBlockEntity extends AbstractAutocannonBreechBlockEntity {

	private Storage<ItemVariant> inventory;


	public AutocannonBreechBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public Storage<ItemVariant> createItemHandler() {
		return this.inventory == null ? this.inventory = new AutocannonBreechInterface(this) : this.inventory;
	}

	@Override
	protected void read(ValueInput tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		if (clientPacket && !this.isVirtual()) AllClientHandle.INSTANCE.queueUpdate(this); // PORT-NOTE(fabric): was NeoForge requestModelDataUpdate
	}

}
