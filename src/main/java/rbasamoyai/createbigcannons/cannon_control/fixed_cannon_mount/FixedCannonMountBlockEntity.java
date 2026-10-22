package rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount;

import rbasamoyai.createbigcannons.base.goggles.CBCGoggleInfo;

import rbasamoyai.createbigcannons.utils.CBCValueIO;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;

import net.minecraft.world.level.storage.ValueInput;

import static rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity.cannonBlockOutsideOfWorld;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.zurrtum.create.AllSoundEvents;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import com.zurrtum.create.content.contraptions.AssemblyException;
import rbasamoyai.createbigcannons.base.goggles.CBCAssemblyExceptionHolder;
import com.zurrtum.create.content.equipment.clipboard.ClipboardCloneable;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;

import com.zurrtum.create.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.cannon_control.ControlPitchContraption;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.ExtendsCannonMount;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.CannonContraptionProviderBlock;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.remix.CBCHasIItemHandlerBlockEntity;

public class FixedCannonMountBlockEntity extends SmartBlockEntity implements CBCAssemblyExceptionHolder,
    ControlPitchContraption.Block, CBCGoggleInfo, CBCHasIItemHandlerBlockEntity, ClipboardCloneable {

	private AssemblyException lastException = null;
	protected PitchOrientedContraptionEntity mountedContraption;
	private boolean running;

	private float cannonYaw;
	private float cannonPitch;
    private boolean reassemble = false;

	private FixedCannonMountScrollValueBehaviour pitchSlot;
	private FixedCannonMountScrollValueBehaviour yawSlot;

	public FixedCannonMountBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		if (CBCBlocks.FIXED_CANNON_MOUNT.has(state))
			this.cannonYaw = state.getValue(FixedCannonMountBlock.FACING).toYRot();
		this.setLazyTickRate(3);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
		behaviours.add(this.pitchSlot = new FixedCannonMountScrollValueBehaviour(this, true));
		behaviours.add(this.yawSlot = new FixedCannonMountScrollValueBehaviour(this, false));
	}

	public void onRedstoneUpdate(boolean assemblyPowered, boolean prevAssemblyPowered, boolean firePowered, boolean prevFirePowered, int firePower) {
		if (assemblyPowered != prevAssemblyPowered || this.reassemble) {
			this.getLevel().setBlock(this.worldPosition, this.getBlockState().setValue(FixedCannonMountBlock.ASSEMBLY_POWERED, assemblyPowered), 3);
			if (assemblyPowered) {
				try {
					this.assemble();
					this.lastException = null;
				} catch (AssemblyException e) {
					this.lastException = e;
					this.sendData();
				}
			} else {
				this.disassemble();
				this.sendData();
			}
		}
		if (firePowered != prevFirePowered) {
			this.getLevel().setBlock(this.worldPosition, this.getBlockState().setValue(FixedCannonMountBlock.FIRE_POWERED, firePowered), 3);
		}
		if (this.running && this.mountedContraption != null && this.getLevel() instanceof ServerLevel slevel) {
			((AbstractMountedCannonContraption) this.mountedContraption.getContraption()).onRedstoneUpdate(slevel, this.mountedContraption, firePowered != prevFirePowered, firePower, this);
		}
	}

	@Override
	public void tick() {
		super.tick();
        if (this.reassemble && this.level instanceof ServerLevel slevel) {
            this.getBlockState().tick(slevel, this.worldPosition, slevel.getRandom());
            this.reassemble = false;
        }

		if (this.mountedContraption != null && !this.mountedContraption.isAlive())
			this.mountedContraption = null;
		this.applyRotation();
	}

	protected void applyRotation() {
		if (this.mountedContraption == null)
			return;
		Direction dir = this.mountedContraption.getInitialOrientation();
		boolean flag = (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) == (dir.getAxis() == Direction.Axis.X);
		float sgn = flag ? 1 : -1;

		float pitchAdjust = this.pitchSlot == null ? 0 : this.pitchSlot.getValue();
		this.mountedContraption.pitch = (this.cannonPitch + pitchAdjust) * sgn;
		float yawAdjust = this.yawSlot == null ? 0 : this.yawSlot.getValue();
		this.mountedContraption.yaw = this.cannonYaw + yawAdjust;

        Entity controller = this.mountedContraption.getControllingPassenger();
        if (controller != null) {
            controller.setXRot(this.mountedContraption.pitch);
            controller.setYRot(this.mountedContraption.yaw);
            controller.xRotO = this.mountedContraption.pitch;
            controller.yRotO = this.mountedContraption.yaw;
        }
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (this.running && this.mountedContraption != null) {
			this.sendData();
		}
	}

	protected void assemble() throws AssemblyException {
		if (!CBCBlocks.FIXED_CANNON_MOUNT.has(this.getBlockState()))
			return;
		Direction facing = this.getBlockState().getValue(BlockStateProperties.FACING);
		BlockPos assemblyPos = this.worldPosition.relative(facing);
		if (this.getLevel().isOutsideBuildHeight(assemblyPos))
			throw cannonBlockOutsideOfWorld(assemblyPos);

		AbstractMountedCannonContraption mountedCannon = this.getContraption(assemblyPos);
		if (mountedCannon == null || !mountedCannon.assemble(this.getLevel(), assemblyPos))
			return;
		Direction facing1 = mountedCannon.initialOrientation();
		this.running = true;

		mountedCannon.removeBlocksFromWorld(this.getLevel(), BlockPos.ZERO);
		PitchOrientedContraptionEntity contraptionEntity = PitchOrientedContraptionEntity.create(this.getLevel(), mountedCannon, facing1, this);
		this.mountedContraption = contraptionEntity;
		this.resetContraptionToOffset();
		this.getLevel().addFreshEntity(contraptionEntity);

		this.sendData();

		AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.getLevel(), this.worldPosition);
	}

	private AbstractMountedCannonContraption getContraption(BlockPos pos) {
		return this.level.getBlockState(pos).getBlock() instanceof CannonContraptionProviderBlock provBlock ? provBlock.getCannonContraption() : null;
	}

	protected void resetContraptionToOffset() {
		if (this.mountedContraption == null)
			return;
		this.cannonPitch = 0;
		this.cannonYaw = this.getContraptionDirection().toYRot();

		this.mountedContraption.pitch = this.cannonPitch;
		this.mountedContraption.yaw = this.cannonYaw;
		this.mountedContraption.prevPitch = this.mountedContraption.pitch;
		this.mountedContraption.prevYaw = this.mountedContraption.yaw;

		this.mountedContraption.setXRot(this.cannonPitch);
		this.mountedContraption.setYRot(this.cannonYaw);
		this.mountedContraption.xRotO = this.mountedContraption.getXRot();
		this.mountedContraption.yRotO = this.mountedContraption.getYRot();

		Direction facing = this.getBlockState().getValue(BlockStateProperties.FACING);
		Vec3 vec = Vec3.atBottomCenterOf((this.worldPosition.relative(facing)));
		this.mountedContraption.setPos(vec);
	}

	public Direction getContraptionDirection() {
		return this.mountedContraption == null ? Direction.NORTH
			: ((AbstractMountedCannonContraption) this.mountedContraption.getContraption()).initialOrientation();
	}

	@Override
	public void disassemble() {
		if (!this.running && this.mountedContraption == null)
			return;
		if (this.mountedContraption != null) {
			this.resetContraptionToOffset();
			this.mountedContraption.saveWithoutId(TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.level.registryAccess())); // Crude refresh of block data
			this.mountedContraption.disassemble();
			AllSoundEvents.CONTRAPTION_DISASSEMBLE.playOnServer(this.getLevel(), this.worldPosition);
		}

		this.running = false;
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return this.lastException;
	}

	@Override
	public boolean isAttachedTo(AbstractContraptionEntity entity) {
		return this.mountedContraption == entity;
	}

	@Override
	public void attach(PitchOrientedContraptionEntity contraption) {
		if (!(contraption.getContraption() instanceof AbstractMountedCannonContraption))
			return;
		this.mountedContraption = contraption;
		if (!this.level.isClientSide()) {
			this.running = true;
			this.sendData();
		}
	}

	@Override
	public void onStall() {
		if (!this.level.isClientSide())
			this.sendData();
	}

	@Override
	public Vec3 getDismountPositionForContraption(PitchOrientedContraptionEntity poce) {
		Direction facing = this.getBlockState().getValue(BlockStateProperties.FACING);
		return Vec3.atBottomCenterOf(this.worldPosition.relative(this.mountedContraption.getInitialOrientation().getOpposite()).relative(facing));
	}

	@Override
	public BlockState getControllerState() {
		return this.getBlockState();
	}

	@Override
	public BlockPos getControllerBlockPos() {
		return this.worldPosition;
	}

    @Override
    public void markForReassembly() {
        this.reassemble = true;
    }

    @Override
	public void remove() {
		this.remove = true;
		if (!this.level.isClientSide())
			this.disassemble();
		super.remove();
	}

	@Override
	protected void write(ValueOutput tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putBoolean("Running", this.running);
		tag.putFloat("CannonYaw", this.cannonYaw);
		tag.putFloat("CannonPitch", this.cannonPitch);
		AssemblyException.write(tag, this.lastException);

        if (this.reassemble)
            tag.putBoolean("TryReassembling", true);
	}

	@Override
	protected void read(ValueInput tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		this.running = tag.getBooleanOr("Running", false);
		this.cannonYaw = tag.getFloatOr("CannonYaw", 0f);
		this.cannonPitch = tag.getFloatOr("CannonPitch", 0f);
		this.lastException = AssemblyException.read(tag);
        this.reassemble = CBCValueIO.contains(tag, "TryReassembling");

		if (!clientPacket) return;

		if (!this.running)
			this.mountedContraption = null;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		ExtendsCannonMount.addCannonInfoToTooltip(tooltip, this.mountedContraption);
		return true;
	}

	public Vec3 getInteractionLocation() {
		return this.mountedContraption != null && this.mountedContraption.getContraption() instanceof AbstractMountedCannonContraption cannon
			? cannon.getInteractionVec(this.mountedContraption) : Vec3.atCenterOf(this.worldPosition);
	}

	@Nullable
	public PitchOrientedContraptionEntity getContraption() {
		return this.mountedContraption;
	}

    @Nullable
    @Override
    public Storage<ItemVariant> getItemHandler(Direction side) {
        return this.mountedContraption == null ? null : this.mountedContraption.getItemHandler();
    }

    @Override public String getClipboardKey() { return "Angles"; }

    @Override
    public boolean writeToClipboard(ValueOutput tag, Direction side) {
        tag.putInt("Pitch", this.pitchSlot.getValue());
        tag.putInt("Yaw", this.yawSlot.getValue());
        return true;
    }

    @Override
    public boolean readFromClipboard(ValueInput tag, Player player, Direction side, boolean simulate) {
        if (simulate)
            return true;
        this.pitchSlot.setValue(tag.getIntOr("Pitch", 0));
        this.yawSlot.setValue(tag.getIntOr("Yaw", 0));
        this.notifyUpdate();
        return true;
    }

    /**
     * Server half of the pitch/yaw adjustment slots.
     *
     * <p>PORT-NOTE(fabric): Create Fly splits scroll values into a server behaviour and a client one (value box, board,
     * formatting), so the client half is {@code fabric.client.FixedCannonMountValueBehaviour}. Create Fly routes value
     * settings packets only to the behaviour registered as {@link ServerScrollValueBehaviour#TYPE}; the pitch slot uses
     * that type and {@code ValueSettingsHandlerMixin} sends packets with the yaw slot's net id to the yaw slot.</p>
     */
    public static class FixedCannonMountScrollValueBehaviour extends ServerScrollValueBehaviour {
		public static final BehaviourType<FixedCannonMountScrollValueBehaviour> YAW_TYPE = new BehaviourType<>();
		public static final int PITCH_NET_ID = 0;
		public static final int YAW_NET_ID = 1;

		private final boolean pitch;

		public FixedCannonMountScrollValueBehaviour(SmartBlockEntity be, boolean pitch) {
			super(be);
			this.pitch = pitch;
			this.between(-45, 45);
		}

		@Override
		public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
			int value = valueSetting.value();
			if (!valueSetting.equals(this.getValueSettings()))
				this.playFeedbackSound(this);
			this.setValue(valueSetting.row() == 0 ? -value : value);
		}

		@Override
		public ValueSettings getValueSettings() {
			return new ValueSettings(this.value < 0 ? 0 : 1, Math.abs(this.value));
		}

		@Override
		public BehaviourType<?> getType() {
			return this.pitch ? TYPE : YAW_TYPE;
		}

		@Override
		public int netId() {
			return this.pitch ? PITCH_NET_ID : YAW_NET_ID;
		}

		@Nullable
		public static FixedCannonMountScrollValueBehaviour get(SmartBlockEntity be, boolean pitch) {
			return pitch ? (be.getBehaviour(TYPE) instanceof FixedCannonMountScrollValueBehaviour slot ? slot : null) : be.getBehaviour(YAW_TYPE);
		}

		public boolean setsPitch() {
			return this.pitch;
		}

		@Override
		public void write(ValueOutput nbt, boolean clientPacket) {
			nbt.putInt(this.pitch ? "PitchAdjustment" : "YawAdjustment", this.value);
		}

		@Override
		public void read(ValueInput nbt, boolean clientPacket) {
			this.value = nbt.getIntOr(this.pitch ? "PitchAdjustment" : "YawAdjustment", 0);
		}
	}

}
