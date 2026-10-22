package rbasamoyai.createbigcannons.munitions.autocannon.ammo_container;

import java.util.List;

import java.util.Iterator;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

/**
 * Item storage for an autocannon ammo container: main ammunition first, then tracers.
 *
 * <p>PORT-NOTE(fabric): Fabric transfer API instead of NeoForge's {@code IItemHandler}; ported from the 1.20.1 branch's
 * {@code fabric/munitions/autocannon/AutocannonAmmoContainerInterface}.</p>
 */
public class AutocannonAmmoContainerInterface extends SnapshotParticipant<AutocannonAmmoContainerInterface.ContainerSnapshot> implements Storage<ItemVariant> {

	private final AutocannonAmmoContainerBlockEntity be;
	private final AutocannonAmmoContainerSlotView[] views;

	public AutocannonAmmoContainerInterface(AutocannonAmmoContainerBlockEntity be) {
		this.be = be;
		this.views = new AutocannonAmmoContainerSlotView[2];
		this.views[0] = new AutocannonAmmoContainerSlotView(this, false);
		this.views[1] = new AutocannonAmmoContainerSlotView(this, true);
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount < 1 || this.be.isCreativeContainer()) return 0;
		ItemStack stack = resource.toStack();
		AutocannonAmmoType ammoType = this.be.getAmmoType();
		if (!ammoType.isValidMunition(stack)) return 0;
		long total = 0;

		ItemStack mainAmmoStack = this.be.getMainAmmoStack();
		if (mainAmmoStack.isEmpty() || resource.matches(mainAmmoStack)) {
			int capacity = ammoType == AutocannonAmmoType.NONE ? AutocannonAmmoType.of(stack).getCapacity()
				: this.be.getMainAmmoCapacity() - mainAmmoStack.getCount();
			int add = (int) Math.min(maxAmount, Math.max(capacity, 0));
			if (add > 0) {
				this.updateSnapshots(transaction);
				this.be.setMainAmmoDirect(resource.toStack(mainAmmoStack.getCount() + add));
				maxAmount -= add;
				total += add;
			}
		}

		ItemStack tracerStack = this.be.getTracerStack();
		if (maxAmount > 0 && (tracerStack.isEmpty() || resource.matches(tracerStack))) {
			int capacity = this.be.getTracerAmmoCapacity() - tracerStack.getCount();
			int add = (int) Math.min(maxAmount, Math.max(capacity, 0));
			if (add > 0) {
				this.updateSnapshots(transaction);
				this.be.setTracersDirect(resource.toStack(tracerStack.getCount() + add));
				total += add;
			}
		}
		return total;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount < 1) return 0;
		boolean isCreative = this.be.isCreativeContainer();
		long total = 0;

		ItemStack mainStack = this.be.getMainAmmoStack();
		if (resource.matches(mainStack)) {
			if (isCreative) return maxAmount;
			long take = Math.min(mainStack.getCount(), maxAmount);
			if (take > 0) {
				this.updateSnapshots(transaction);
				this.be.setMainAmmoDirect(mainStack.copyWithCount(mainStack.getCount() - (int) take));
				total += take;
			}
		}

		ItemStack tracerStack = this.be.getTracerStack();
		if (total < maxAmount && resource.matches(tracerStack)) {
			if (isCreative) return maxAmount;
			long take = Math.min(tracerStack.getCount(), maxAmount - total);
			if (take > 0) {
				this.updateSnapshots(transaction);
				this.be.setTracersDirect(tracerStack.copyWithCount(tracerStack.getCount() - (int) take));
				total += take;
			}
		}
		return total;
	}

	@NotNull
	public ItemStack getStack(boolean isTracers) {
		return isTracers ? this.be.getTracerStack() : this.be.getMainAmmoStack();
	}

	public int getCapacityForSlot(boolean isTracer) {
		return isTracer ? this.be.getTracerAmmoCapacity() : this.be.getMainAmmoCapacity();
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return List.<StorageView<ItemVariant>>of(this.views).iterator();
	}

	@Override protected ContainerSnapshot createSnapshot() { return new ContainerSnapshot(this); }
	@Override protected void readSnapshot(ContainerSnapshot snapshot) { snapshot.apply(this); }

	@Override
	protected void onFinalCommit() {
		this.be.notifyUpdate();
	}

	public static class ContainerSnapshot {
		private final ItemStack ammo;
		private final ItemStack tracers;

		public ContainerSnapshot(AutocannonAmmoContainerInterface inventory) {
			this.ammo = inventory.be.getMainAmmoStack().copy();
			this.tracers = inventory.be.getTracerStack().copy();
		}

		public void apply(AutocannonAmmoContainerInterface inventory) {
			inventory.be.setMainAmmoDirect(this.ammo);
			inventory.be.setTracersDirect(this.tracers);
		}
	}

}
