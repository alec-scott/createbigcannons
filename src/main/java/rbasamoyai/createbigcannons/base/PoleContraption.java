package rbasamoyai.createbigcannons.base;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.content.contraptions.AssemblyException;
import com.zurrtum.create.content.contraptions.TranslatingContraption;

import com.zurrtum.create.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class PoleContraption extends TranslatingContraption {

	protected int extensionLength;
	protected int initialExtensionProgress;
	protected Direction orientation;
	protected AABB pistonContraptionHitbox;
	protected boolean retract;

	public PoleContraption() {
	}

	public PoleContraption(Direction orientation, boolean retract) {
		this.orientation = orientation;
		this.retract = retract;
	}

	@Override
	public boolean assemble(Level level, BlockPos pos) throws AssemblyException {
		if (!this.collectExtensions(level, pos, this.orientation)) return false;
		int count = this.blocks.size();

		if (!this.searchMovedStructure(level, this.anchor, this.retract ? this.orientation.getOpposite() : this.orientation)) {
			return false;
		}
		this.bounds = this.blocks.size() == count ? this.pistonContraptionHitbox : this.bounds.minmax(this.pistonContraptionHitbox);
		return true;
	}

	protected abstract boolean collectExtensions(Level level, BlockPos pos, Direction direction) throws AssemblyException;

	@Override
	protected boolean isAnchoringBlockAt(BlockPos pos) {
		return this.pistonContraptionHitbox.contains(VecHelper.getCenterOf(pos.subtract(this.anchor)));
	}

	@Override
	public void read(Level level, ValueInput tag, boolean spawnData) {
		super.read(level, tag, spawnData);
		this.initialExtensionProgress = tag.getIntOr("InitialLength", 0);
		this.extensionLength = tag.getIntOr("ExtensionLength", 0);
		this.orientation = Direction.from3DDataValue(tag.getIntOr("Orientation", 0));
	}

	@Override
	public void write(ValueOutput tag, boolean spawnPacket) {
		super.write(tag, spawnPacket);
		tag.putInt("InitialLength", this.initialExtensionProgress);
		tag.putInt("ExtensionLength", this.extensionLength);
		tag.putInt("Orientation", this.orientation.get3DDataValue());
	}

	public int extensionLength() {
		return this.extensionLength;
	}

	public int initialExtensionProgress() {
		return this.initialExtensionProgress;
	}

	public Direction orientation() {
		return this.orientation;
	}

}
