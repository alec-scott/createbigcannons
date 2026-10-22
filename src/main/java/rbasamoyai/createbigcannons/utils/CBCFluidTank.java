package rbasamoyai.createbigcannons.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.zurrtum.create.foundation.fluid.FluidTank;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

/**
 * A single fluid tank with a change callback and a validator, keeping the NeoForge {@code FluidTank} /
 * Create {@code SmartFluidTank} calls CBC uses ({@code fill}, {@code drain}, {@code setValidator}).
 *
 * <p>PORT-NOTE(fabric): built on Create Fly's {@link FluidTank}, so pipes and spouts reach it through
 * {@code FluidInventoryStorage.of(tank, side)}. Amounts are Fabric droplets (81 per millibucket).</p>
 */
public class CBCFluidTank extends FluidTank {

	public enum FluidAction {
		EXECUTE, SIMULATE;

		public boolean execute() { return this == EXECUTE; }
		public boolean simulate() { return this == SIMULATE; }
	}

	private final Consumer<FluidStack> onChanged;
	private Predicate<FluidStack> validator = stack -> true;

	public CBCFluidTank(int capacity) {
		this(capacity, stack -> {});
	}

	public CBCFluidTank(int capacity, Consumer<FluidStack> onChanged) {
		super(capacity);
		this.onChanged = onChanged;
	}

	public CBCFluidTank setValidator(Predicate<FluidStack> validator) {
		this.validator = validator;
		return this;
	}

	@Override
	public boolean isValid(int slot, FluidStack stack) {
		return this.validator.test(stack);
	}

	@Override
	public void markDirty() {
		this.onChanged.accept(this.getFluid());
	}

	public int getCapacity() {
		return this.capacity;
	}

	public int getFluidAmount() {
		return this.fluid.getAmount();
	}

	public int getSpace() {
		return Math.max(0, this.capacity - this.fluid.getAmount());
	}

	/** @return the amount that was (or would be) filled */
	public int fill(FluidStack stack, FluidAction action) {
		if (stack.isEmpty() || !this.validator.test(stack)) return 0;
		if (!this.fluid.isEmpty() && !FluidStack.areFluidsAndComponentsEqualIgnoreCapacity(this.fluid, stack)) return 0;
		int filled = Math.min(this.getSpace(), stack.getAmount());
		if (filled <= 0 || action.simulate()) return filled;
		if (this.fluid.isEmpty()) {
			FluidStack copy = stack.copy();
			copy.setAmount(filled);
			this.setFluid(copy);
		} else {
			this.fluid.setAmount(this.fluid.getAmount() + filled);
		}
		this.markDirty();
		return filled;
	}

	public FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain <= 0 || this.fluid.isEmpty()) return FluidStack.EMPTY;
		int drained = Math.min(maxDrain, this.fluid.getAmount());
		FluidStack result = this.fluid.copy();
		result.setAmount(drained);
		if (action.execute()) {
			int remaining = this.fluid.getAmount() - drained;
			if (remaining <= 0) {
				this.setFluid(FluidStack.EMPTY);
			} else {
				this.fluid.setAmount(remaining);
			}
			this.markDirty();
		}
		return result;
	}

	/** Same layout as NeoForge's {@code FluidTank.writeToNBT} ({@code "Fluid"} key), for item components. */
	public CompoundTag writeToNBT(HolderLookup.Provider registries, CompoundTag tag) {
		if (!this.fluid.isEmpty())
			tag.put("Fluid", FluidStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.fluid).getOrThrow());
		return tag;
	}

	public void readFromNBT(HolderLookup.Provider registries, CompoundTag tag) {
		Tag fluidTag = tag.get("Fluid");
		this.setFluid(fluidTag == null ? FluidStack.EMPTY
			: FluidStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), fluidTag).result().orElse(FluidStack.EMPTY));
	}

	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !FluidStack.areFluidsAndComponentsEqualIgnoreCapacity(this.fluid, resource)) return FluidStack.EMPTY;
		return this.drain(resource.getAmount(), action);
	}

}
