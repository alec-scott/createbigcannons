package rbasamoyai.createbigcannons.munitions.autocannon.ammo_container;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/** A view of the main-ammo or tracer slot; changes go through {@link AutocannonAmmoContainerInterface}. */
public class AutocannonAmmoContainerSlotView implements StorageView<ItemVariant> {

	protected final AutocannonAmmoContainerInterface inventory;
	protected final boolean isTracer;

	public AutocannonAmmoContainerSlotView(AutocannonAmmoContainerInterface inventory, boolean isTracer) {
		this.inventory = inventory;
		this.isTracer = isTracer;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return this.inventory.extract(resource, maxAmount, transaction);
	}

	@Override public boolean isResourceBlank() { return this.getResource().isBlank(); }
	@Override public ItemVariant getResource() { return ItemVariant.of(this.inventory.getStack(this.isTracer)); }
	@Override public long getAmount() { return this.inventory.getStack(this.isTracer).getCount(); }
	@Override public long getCapacity() { return this.inventory.getCapacityForSlot(this.isTracer); }

}
