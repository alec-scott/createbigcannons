package rbasamoyai.createbigcannons.fabric.client;

import com.google.common.collect.ImmutableList;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsBoard;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsFormatter;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity.FixedCannonMountScrollValueBehaviour;

/**
 * Client half of the fixed cannon mount's pitch/yaw adjustment slots (value box, board and formatting).
 *
 * <p>PORT-NOTE(fabric): split from upstream's {@code FixedCannonMountScrollValueBehaviour}, which extended Create's
 * valve-handle scroll behaviour; Create Fly keeps that half client-only.</p>
 */
public class FixedCannonMountValueBehaviour extends ScrollValueBehaviour<FixedCannonMountBlockEntity, FixedCannonMountScrollValueBehaviour> {

	public static final BehaviourType<FixedCannonMountValueBehaviour> PITCH_TYPE = new BehaviourType<>();
	public static final BehaviourType<FixedCannonMountValueBehaviour> YAW_TYPE = new BehaviourType<>();

	private final boolean pitch;

	public FixedCannonMountValueBehaviour(FixedCannonMountBlockEntity be, boolean pitch) {
		super(CreateLang.builder(CreateBigCannons.MOD_ID).translate("fixed_cannon_mount.angle_" + (pitch ? "pitch" : "yaw")).component(),
			be, new FixedCannonMountValueBox(pitch));
		this.pitch = pitch;
		this.behaviour = FixedCannonMountScrollValueBehaviour.get(be, pitch);
		this.withFormatter(v -> String.format("%s%d", v < 0 ? "-" : v > 0 ? "+" : "", Math.abs(v))
			+ CreateLang.translateDirect("generic.unit.degrees").getString());
	}

	public static FixedCannonMountValueBehaviour pitch(FixedCannonMountBlockEntity be) {
		return new FixedCannonMountValueBehaviour(be, true);
	}

	public static FixedCannonMountValueBehaviour yaw(FixedCannonMountBlockEntity be) {
		return new FixedCannonMountValueBehaviour(be, false);
	}

	@Override
	public void initialize() {
		if (this.behaviour == null)
			this.behaviour = FixedCannonMountScrollValueBehaviour.get(this.blockEntity, this.pitch);
	}

	public MutableComponent formatValue(ValueSettings settings) {
		int sgn = settings.row() == 0 ? -1 : 1;
		return CreateLang.number(settings.value() * sgn)
			.add(CreateLang.translateDirect("generic.unit.degrees"))
			.component();
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		ImmutableList<Component> rows = ImmutableList.of(Component.literal("-")
				.withStyle(ChatFormatting.BOLD),
			Component.literal("+")
				.withStyle(ChatFormatting.BOLD));
		return new ValueSettingsBoard(this.label, 45, 15, rows, new ValueSettingsFormatter(this::formatValue));
	}

	@Override
	public BehaviourType<?> getType() {
		return this.pitch ? PITCH_TYPE : YAW_TYPE;
	}

	public static class FixedCannonMountValueBox extends CenteredSideValueBoxTransform {
		private final boolean pitch;

		public FixedCannonMountValueBox(boolean pitch) {
			super((state, dir) -> state.getValue(BlockStateProperties.FACING) != dir);
			this.pitch = pitch;
		}

		@Override
		protected Vec3 getSouthLocation() {
			double xOffset = this.pitch ? -4 : 4;
			return VecHelper.voxelSpace(8 + xOffset, 8, 15.5);
		}
	}

}
