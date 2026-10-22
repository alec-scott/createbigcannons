package rbasamoyai.createbigcannons.crafting.casting;

import org.jetbrains.annotations.Nullable;

import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.catnip.data.Pair;
import com.zurrtum.create.content.fluids.transfer.GenericItemEmptying;
import com.zurrtum.create.content.fluids.transfer.GenericItemFilling;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.transfer.FluidInventoryStorage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.EndFluidStack;
import rbasamoyai.createbigcannons.utils.CBCFluidTank;
import rbasamoyai.createbigcannons.utils.CBCFluidTank.FluidAction;
import rbasamoyai.createbigcannons.utils.FluidUnits;

/**
 * PORT-NOTE(fabric): NeoForge fluid capability -> Fabric {@code FluidStorage} on the top face (see
 * {@link #registerStorage()}); tank is a {@link CBCFluidTank} in droplets. CBC's millibucket constants go through
 * {@link FluidUnits}.
 */
public class CannonCastBlockEntity extends AbstractCannonCastBlockEntity {

	protected CBCFluidTank fluid;
	protected FluidStack leakage = FluidStack.EMPTY;

	public CannonCastBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.fluid = new CBCFluidTank(1, this::onFluidStackChanged);
		this.refreshCap();
	}

	public static void registerStorage() {
		FluidStorage.SIDED.registerForBlockEntity((be, dir) -> {
			if (dir != Direction.UP)
				return null;
			return FluidInventoryStorage.of(be.getTankForCap(), dir);
		}, CBCBlockEntities.CANNON_CAST.get());
	}

	@Override
	protected void refreshCap() {
		// Fabric API lookups are not cached per block entity, so there is nothing to invalidate.
	}

	private CBCFluidTank getTankForCap() {
		return this.isController() ? this.fluid :
			this.getControllerBE() instanceof CannonCastBlockEntity controller ? controller.getTankForCap() : this.fluid;
	}

	@Override
	protected void updateFluids(ValueInput tag) {
		this.fluid.setCapacity(this.calculateCapacityFromStructure());
		tag.child("FluidContent").ifPresent(this.fluid::read);
		this.leakage = tag.read("Leakage", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
	}

	@Override
	protected void updateFluidClient() {
		this.fluid.setCapacity(this.calculateCapacityFromStructure());
	}

	@Override
	protected void writeFluidToTag(ValueOutput tag) {
		this.fluid.write(tag.child("FluidContent"));
		tag.store("Leakage", FluidStack.OPTIONAL_CODEC, this.leakage);
	}

	protected void onFluidStackChanged(FluidStack stack) {
		if (!this.hasLevel()) return;

		boolean isLarge = this.castShape != null && this.castShape.isLarge();
		int horizStart = isLarge ? -1 : 0;
		int horizEnd = isLarge ? 1 : 2;

		for (int yOffset = 0; yOffset < this.height; yOffset++) {
			for (int xOffset = -horizStart; xOffset < horizEnd; xOffset++) {
				for (int zOffset = -horizStart; zOffset < horizEnd; zOffset++) {
					BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
					AbstractCannonCastBlockEntity castAt = ConnectivityHandler.partAt(this.getType(), this.getLevel(), pos);
					if (castAt == null) continue;
					this.getLevel().updateNeighbourForOutputSignal(pos, castAt.getBlockState().getBlock());
				}
			}
		}

		if (!this.getLevel().isClientSide()) {
			this.notifyUpdate();
		}

		if (this.isVirtual()) {
			if (this.fluidLevel == null) {
				this.fluidLevel = LerpedFloat.linear().startWithValue(this.getFillState());
			}
			this.fluidLevel.chase(this.getFillState(), 0.5f, LerpedFloat.Chaser.EXP);
		}
	}

	@Override
	protected void leakContents() {
		FluidStack fstack = this.fluid.drain(FluidUnits.fromMb(20), FluidAction.EXECUTE);
		if (!fstack.isEmpty()) {
			if (this.leakage.isEmpty()) {
				this.leakage = fstack;
			} else {
				this.leakage.setAmount(this.leakage.getAmount() + fstack.getAmount());
			}
		}
		if (this.leakage.getAmount() >= FluidUnits.fromMb(1500)) {
			net.minecraft.world.level.material.Fluid leakFluid = this.leakage.getFluid();
			this.getLevel().setBlock(this.worldPosition.below(), leakFluid.defaultFluidState().createLegacyBlock(), 11);
			this.leakage.setAmount(this.leakage.getAmount() - FluidUnits.fromMb(1500));
		}
	}

	@Override
	protected boolean canStartCasting() {
		return this.fluid.getFluidAmount() >= this.fluid.getCapacity() && !this.fluid.isEmpty();
	}

	@Override
	protected net.minecraft.world.level.material.Fluid getFluid() {
		return this.fluid.getFluid().getFluid();
	}

	@Override
	protected InvalidCastingError createInvalidCastingError(BlockPos pos, CannonCastShape shape) {
		FluidStack stack = this.fluid.getFluid();
		return new InvalidCastingError(pos, new EndFluidStack(stack.getFluid(), FluidUnits.toMb(stack.getAmount()), stack.getComponents()), shape);
	}

	@Override
	protected void addStructureCapacityToController(AbstractCannonCastBlockEntity controller) {
		if (controller instanceof CannonCastBlockEntity cController) {
			cController.fluid.setCapacity(cController.fluid.getCapacity() + FluidUnits.fromMb(this.castShape.fluidSize()));
		}
	}

	@Override
	protected void reInitTank() {
		this.fluid = new CBCFluidTank(FluidUnits.fromMb(this.castShape.fluidSize()), this::onFluidStackChanged);
	}

	@Override
	protected void mergeControllerAndOtherFluids(AbstractCannonCastBlockEntity controller, AbstractCannonCastBlockEntity otherCast) {
		if (controller instanceof CannonCastBlockEntity cController && otherCast instanceof CannonCastBlockEntity cOther) {
			cController.fluid.setCapacity(cController.fluid.getCapacity() + cOther.fluid.getCapacity());
			cController.fluid.fill(cOther.fluid.drain(cOther.fluid.getCapacity(), FluidAction.EXECUTE), FluidAction.EXECUTE);
			cOther.fluid = new CBCFluidTank(1);
		}
	}

	@Override
	protected void onDestroyCenterCast() {
		CannonCastBlockEntity controller = (CannonCastBlockEntity) this.getControllerBE();
		if (controller == null) return;
		int thisIndex = this.worldPosition.getY() - controller.worldPosition.getY();

		controller.height -= 1;
		int capacityUpTo = FluidUnits.fromMb(controller.structure.subList(0, Mth.clamp(thisIndex, 0, controller.structure.size()))
			.stream()
			.map(CannonCastShape::fluidSize)
			.reduce(Integer::sum)
			.orElse(0));
		int thisCapacity = FluidUnits.fromMb(this.castShape.fluidSize());
		int leakAmount = Mth.clamp(controller.fluid.getFluidAmount() - capacityUpTo, 0, thisCapacity);
		FluidStack addLeak = controller.fluid.drain(leakAmount, FluidAction.EXECUTE);
		controller.fluid.setCapacity(Math.max(1, controller.fluid.getCapacity() - thisCapacity));
		FluidStack remaining = controller.fluid.getFluid();

		if (controller == this && this.height > 0) {
			if (this.getLevel().getBlockEntity(this.worldPosition.above()) instanceof CannonCastBlockEntity otherCast) {
				otherCast.controllerPos = null;
				otherCast.height = this.height;
				otherCast.structure = getStructureFromPoint(this.getLevel(), this.worldPosition.above(), this.height);
				otherCast.fluid = new CBCFluidTank(otherCast.calculateCapacityFromStructure(), otherCast::onFluidStackChanged);
				otherCast.fluid.fill(remaining, FluidAction.EXECUTE);
				otherCast.updatePotentialCastsAbove();
				otherCast.notifyUpdate();
			}
		} else {
			int oldHeight = controller.height;
			controller.height = thisIndex;
			controller.structure = controller.structure.subList(0, Mth.clamp(thisIndex, 0, controller.structure.size()));
			controller.fluid = new CBCFluidTank(controller.calculateCapacityFromStructure(), controller::onFluidStackChanged);
			int firstRemaining = remaining.getAmount() - controller.fluid.fill(remaining, FluidAction.EXECUTE);
			if (!remaining.isEmpty()) remaining.setAmount(firstRemaining);
			controller.updateRecipes = true;
			controller.notifyUpdate();

			if (this.getLevel().getBlockEntity(this.worldPosition.above()) instanceof CannonCastBlockEntity otherCast) {
				otherCast.controllerPos = null;
				otherCast.height = oldHeight - controller.height;
				otherCast.structure = getStructureFromPoint(this.getLevel(), this.worldPosition.above(), otherCast.height);
				otherCast.fluid = new CBCFluidTank(otherCast.calculateCapacityFromStructure(), otherCast::onFluidStackChanged);
				otherCast.fluid.fill(remaining, FluidAction.EXECUTE);
				otherCast.updatePotentialCastsAbove();
				otherCast.updateRecipes = true;
				otherCast.notifyUpdate();
			}
		}

		if (this.castShape.isLarge()) {
			for (BlockPos pos : BlockPos.betweenClosed(this.worldPosition.offset(-1, 0, -1), this.worldPosition.offset(1, 0, 1))) {
				if (this.getLevel().getBlockState(pos).is(CBCBlocks.CANNON_CAST.get()))
					this.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
			}
		}

		if (!addLeak.isEmpty()) {
			net.minecraft.world.level.material.Fluid fluid = addLeak.getFluid();
			int amountMb = FluidUnits.toMb(addLeak.getAmount()) - 250;
			if (fluid instanceof FlowingFluid flowing) {
				int fluidLevel = Math.max(Math.floorDiv(amountMb * 8, 1000), 0);
				if (fluidLevel > 0) {
					this.level.setBlock(this.worldPosition, flowing.getFlowing(fluidLevel, this.level.getBlockState(this.worldPosition.below()).isAir()).createLegacyBlock(), 11);
				}
			} else if (amountMb >= 1000) {
				this.level.setBlock(this.worldPosition, addLeak.getFluid().defaultFluidState().createLegacyBlock(), 11);
			}
		}
	}

	@Override
	public float getFillState() {
		return this.fluid.getCapacity() == 0 ? 0.0f : (float) this.fluid.getFluidAmount() / (float) this.fluid.getCapacity();
	}

	@Override
	protected boolean testWithFluid(CannonCastingRecipe recipe) {
		CannonCastBlockEntity cController = (CannonCastBlockEntity) this.getControllerBE();
		if (cController == null || cController.fluid.getFluid().isEmpty()) return false;
		return recipe.ingredient().test(cController.fluid.getFluid());
	}

	@Override
	public boolean tryEmptyItemIntoBE(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem, Direction side) {
		if (!GenericItemEmptying.canItemBeEmptied(worldIn, heldItem)) return false;
		if (worldIn.isClientSide()) return true;

		Pair<FluidStack, ItemStack> emptyingResult = GenericItemEmptying.emptyItem(worldIn, heldItem, true);
		FluidStack fluidStack = emptyingResult.getFirst();

		int fillAmount = this.fluid.fill(fluidStack, FluidAction.SIMULATE);
		if (fluidStack.getAmount() != fillAmount && (!player.isCreative() || fillAmount < 1)) {
			return false; // Enable top-up filling in creative
		}

		ItemStack copyOfHeld = heldItem.copy();
		emptyingResult = GenericItemEmptying.emptyItem(worldIn, copyOfHeld, false);
		this.fluid.fill(fluidStack, FluidAction.EXECUTE);

		if (!player.isCreative()) {
			if (copyOfHeld.isEmpty())
				player.setItemInHand(handIn, emptyingResult.getSecond());
			else {
				player.setItemInHand(handIn, copyOfHeld);
				player.getInventory().placeItemBackInInventory(emptyingResult.getSecond());
			}
		}
		return true;
	}

	@Override
	public boolean tryFillItemFromBE(Level level, Player player, InteractionHand handIn, ItemStack heldItem, Direction side) {
		if (!GenericItemFilling.canItemBeFilled(level, heldItem)) return false;
		if (level.isClientSide()) return true;

		FluidStack fluid = this.fluid.getFluid();
		if (fluid.isEmpty()) return false;
		int requiredAmountForItem = GenericItemFilling.getRequiredAmountForItem(level, heldItem, fluid.copy());
		if (requiredAmountForItem == -1 || requiredAmountForItem > fluid.getAmount()) return false;

		if (player.isCreative()) heldItem = heldItem.copy();
		ItemStack out = GenericItemFilling.fillItem(level, requiredAmountForItem, heldItem, fluid.copy());

		FluidStack copy = fluid.copy();
		copy.setAmount(requiredAmountForItem);
		this.fluid.drain(copy, FluidAction.EXECUTE);

		if (!player.isCreative()) player.getInventory().placeItemBackInInventory(out);
		this.notifyUpdate();
		return true;
	}

}
