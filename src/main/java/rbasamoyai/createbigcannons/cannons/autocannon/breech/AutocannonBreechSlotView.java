package rbasamoyai.createbigcannons.cannons.autocannon.breech;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/** A read view of one breech slot (input queue tail or output); changes go through {@link AutocannonBreechInterface}. */
public class AutocannonBreechSlotView implements StorageView<ItemVariant> {

	protected final AutocannonBreechInterface inventory;
	protected final boolean isInput;

	public AutocannonBreechSlotView(AutocannonBreechInterface inventory, boolean isInput) {
		this.inventory = inventory;
		this.isInput = isInput;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return this.isInput ? 0 : this.inventory.extract(resource, maxAmount, transaction);
	}

	@Override public boolean isResourceBlank() { return this.getResource().isBlank(); }
	@Override public ItemVariant getResource() { return ItemVariant.of(this.inventory.getStack(this.isInput)); }
	@Override public long getAmount() { return this.inventory.getStack(this.isInput).getCount(); }
	@Override public long getCapacity() { return 1; }

}
