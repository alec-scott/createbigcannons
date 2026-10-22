package rbasamoyai.createbigcannons.crafting.boring;

import rbasamoyai.createbigcannons.utils.CBCValueIO;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import java.util.List;

import com.zurrtum.create.content.kinetics.base.DirectionalAxisKineticBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import com.zurrtum.create.infrastructure.transfer.FluidInventoryStorage;
import rbasamoyai.createbigcannons.fabric.client.CBCGoggleHelper;
import rbasamoyai.createbigcannons.utils.CBCFluidTank;
import rbasamoyai.createbigcannons.utils.CBCFluidTank.FluidAction;
import rbasamoyai.createbigcannons.utils.FluidUnits;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;

public class CannonDrillBlockEntity extends AbstractCannonDrillBlockEntity {

	protected CBCFluidTank lubricant;

	public CannonDrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.lubricant = new CBCFluidTank(FluidUnits.fromMb(1000), this::onFluidStackChanged).setValidator(fs -> fs.getFluid() == Fluids.WATER);
	}

    /** PORT-NOTE(fabric): NeoForge fluid capability -> Fabric FluidStorage on the lubricant pipe axis. */
    public static void registerStorage() {
        FluidStorage.SIDED.registerForBlockEntity((be, dir) -> {
            if (dir == null)
                return null;
            Direction facing = be.getBlockState().getValue(BlockStateProperties.FACING);
            boolean alongFirst = be.getBlockState().getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
            Direction.Axis pipeAxis = switch (facing.getAxis()) {
                case X -> alongFirst ? Direction.Axis.Z : Direction.Axis.Y;
                case Y -> alongFirst ? Direction.Axis.Z : Direction.Axis.X;
                default -> alongFirst ? Direction.Axis.Y : Direction.Axis.X;
            };
            return pipeAxis == dir.getAxis() && be instanceof CannonDrillBlockEntity drill ? FluidInventoryStorage.of(drill.lubricant, dir) : null;
        }, CBCBlockEntities.CANNON_DRILL.get());
    }

	protected void onFluidStackChanged(FluidStack newStack) {
		if (this.hasLevel() && !this.getLevel().isClientSide()) {
			this.notifyUpdate();
		}
	}

	@Override
	protected void read(ValueInput compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		compound.child("FluidContent").ifPresent(this.lubricant::read);
	}

	@Override
	protected void write(ValueOutput compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		this.lubricant.write(compound.child("FluidContent"));
	}

	@Override
	protected boolean drainLubricant(int drainSpeed) {
		int drain = FluidUnits.fromMb(drainSpeed);
		return this.lubricant.drain(drain, FluidAction.EXECUTE).getAmount() < drain;
	}

	@Override
	protected void addFluidInfoToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		CBCGoggleHelper.containedFluidTooltip(tooltip, isPlayerSneaking, this.lubricant);
	}

}
