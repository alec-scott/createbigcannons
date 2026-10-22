package rbasamoyai.createbigcannons.cannon_control.cannon_mount;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import rbasamoyai.createbigcannons.remix.CBCHasIItemHandlerBlockEntity;

public class CannonMountExtensionBlockEntity extends KineticBlockEntity implements ExtendsCannonMount, CBCHasIItemHandlerBlockEntity {

	public CannonMountExtensionBlockEntity(BlockEntityType<? extends CannonMountExtensionBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Nullable
	@Override
	public CannonMountBlockEntity getCannonMount() {
		if (this.level == null)
			return null;
		Direction dir = this.getBlockState().getValue(CannonMountExtensionBlock.FACING);
		BlockPos pos = this.worldPosition.relative(dir);
		BlockState attachedTo = this.level.getBlockState(pos);
		if (!(attachedTo.getBlock() instanceof IRotate rotate) || !rotate.hasShaftTowards(this.level, pos, attachedTo, dir.getOpposite()))
			return null;
		return this.level.getBlockEntity(pos) instanceof CannonMountBlockEntity mount ? mount : null;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (!super.addToGoggleTooltip(tooltip, isPlayerSneaking)) return false;
		CannonMountBlockEntity mount = this.getCannonMount();
		if (mount != null) ExtendsCannonMount.addCannonInfoToTooltip(tooltip, mount.mountedContraption);
		return true;
	}

    @Override
    @Nullable
    public Storage<ItemVariant> getItemHandler(Direction side) {
        CannonMountBlockEntity cannonMount = this.getCannonMount();
        return cannonMount == null ? null : cannonMount.getItemHandler(side);
    }

}
