package rbasamoyai.createbigcannons.crafting.casting;

import rbasamoyai.createbigcannons.utils.CBCValueIO;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import java.util.List;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createbigcannons.base.CBCRegistries;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class FinishedCannonCastBlockEntity extends SmartBlockEntity {

	private CannonCastShape renderedShape = CannonCastShape.VERY_SMALL;
	private BlockPos centralBlock;
	private BlockPos rootBlock;
	private int height;

	public FinishedCannonCastBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
	}

	public void setCentralBlock(BlockPos pos) {
		this.centralBlock = pos;
	}

	public boolean isCentralBlock() {
		return this.centralBlock == null;
	}

	public void setRootBlock(BlockPos pos) {
		this.rootBlock = pos;
	}

	public BlockPos getRootBlock() {
		return this.rootBlock == null ? this.worldPosition : this.rootBlock;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setRenderedShape(CannonCastShape shape) {
		this.renderedShape = shape;
	}

	public CannonCastShape getRenderedShape() {
		return this.renderedShape;
	}

	@Override
	public void initialize() {
		super.initialize();
		this.sendData();
		if (this.getLevel().isClientSide()) this.invalidateRenderBoundingBox();
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return this.isCentralBlock() ? new AABB(this.getRootBlock()).expandTowards(2, this.height - 1, 2) : super.createRenderBoundingBox();
	}

	public void removeCast() {
		this.setRemoved();
		if (this.isCentralBlock()) {
			this.getLevel().playSound(null, this.worldPosition.offset(1, 0, 1), this.getBlockState().getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
			for (BlockPos pos : BlockPos.betweenClosed(this.worldPosition, this.worldPosition.offset(2, 0, 2))) {
				BlockPos posI = pos.immutable();
				if (posI.equals(this.worldPosition.offset(1, 0, 1))) continue;
				this.getLevel().setBlock(posI, Blocks.AIR.defaultBlockState(), 11);
			}
		} else if (this.getLevel().getBlockEntity(this.centralBlock) instanceof FinishedCannonCastBlockEntity cast && cast.isCentralBlock()) {
			cast.removeCast();
		}
	}

	@Override
	protected void write(ValueOutput tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		if (this.isCentralBlock()) {
			tag.store("RootBlock", BlockPos.CODEC, this.getRootBlock());
			tag.putInt("Height", this.height);
			tag.putString("RenderedShape", CBCRegistries.cannonCastShapes().getKey(this.renderedShape).toString());
		} else {
			tag.store("CentralBlock", BlockPos.CODEC, this.centralBlock);
		}
	}

	@Override
	protected void read(ValueInput tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		this.centralBlock = CBCValueIO.contains(tag, "CentralBlock") ? tag.read("CentralBlock", BlockPos.CODEC).get() : null;
		this.rootBlock = CBCValueIO.contains(tag, "RootBlock") ? tag.read("RootBlock", BlockPos.CODEC).get() : this.worldPosition;
		this.height = tag.getIntOr("Height", 0);
		this.renderedShape = CBCValueIO.contains(tag, "RenderedShape")
			? CBCRegistries.cannonCastShapes().get(CBCUtils.location(tag.getStringOr("RenderedShape", "")))
			: CannonCastShape.VERY_SMALL;
	}

}
