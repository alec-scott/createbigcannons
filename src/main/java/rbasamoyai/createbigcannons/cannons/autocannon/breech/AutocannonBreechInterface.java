package rbasamoyai.createbigcannons.cannons.autocannon.breech;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.StorageViewArrayIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;

/**
 * Item storage for an autocannon breech: insert ammunition into the input queue, extract spent casings.
 *
 * <p>PORT-NOTE(fabric): Fabric transfer API instead of NeoForge's {@code IItemHandler}; ported from the 1.20.1 branch's
 * {@code fabric/cannons/AutocannonBreechInterface}.</p>
 */
public class AutocannonBreechInterface extends SnapshotParticipant<AutocannonBreechInterface.BreechSnapshot> implements Storage<ItemVariant> {

	private final AbstractAutocannonBreechBlockEntity breech;
	private final AutocannonBreechSlotView[] views;

	public AutocannonBreechInterface(AbstractAutocannonBreechBlockEntity breech) {
		this.breech = breech;
		this.views = new AutocannonBreechSlotView[2];
		this.views[0] = new AutocannonBreechSlotView(this, true);
		this.views[1] = new AutocannonBreechSlotView(this, false);
	}

	public boolean isItemValid(@NotNull ItemStack stack) { return stack.getItem() instanceof AutocannonAmmoItem; }

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount < 1) return 0;
		ItemStack stack = resource.toStack();
		if (!this.isItemValid(stack) || this.breech.isInputFull()) return 0;
		Deque<ItemStack> input = this.breech.getInputBuffer();
		long maxCount = Math.min(this.breech.getQueueLimit() - input.size(), maxAmount);
		if (maxCount < 1) return 0;
		this.updateSnapshots(transaction);
		for (long i = 0; i < maxCount; ++i)
			input.add(stack.copyWithCount(1));
		return maxCount;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		ItemStack output = this.breech.getOutputBuffer();
		if (maxAmount < 1 || output.isEmpty() || !resource.matches(output)) return 0;
		this.updateSnapshots(transaction);
		output.split(1);
		return 1;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return new StorageViewArrayIterator<>(this.views);
	}

	@Override protected BreechSnapshot createSnapshot() { return new BreechSnapshot(this); }
	@Override protected void readSnapshot(BreechSnapshot snapshot) { snapshot.apply(this); }

	@NotNull
	public ItemStack getStack(boolean isInput) {
		if (isInput) {
			Deque<ItemStack> inputBuffer = this.breech.getInputBuffer();
			return this.breech.isInputFull() && !inputBuffer.isEmpty() ? inputBuffer.peekLast() : ItemStack.EMPTY;
		}
		return this.breech.getOutputBuffer();
	}

	public static class BreechSnapshot {
		private final List<ItemStack> inputBuffer;
		private final ItemStack outputBuffer;

		public BreechSnapshot(AutocannonBreechInterface inventory) {
			this.inputBuffer = inventory.breech.getInputBuffer().stream().map(ItemStack::copy).toList();
			this.outputBuffer = inventory.breech.getOutputBuffer().copy();
		}

		public void apply(AutocannonBreechInterface inventory) {
			inventory.breech.getInputBuffer().clear();
			inventory.breech.getInputBuffer().addAll(this.inputBuffer);
			inventory.breech.setOutputBuffer(this.outputBuffer);
		}
	}

}
