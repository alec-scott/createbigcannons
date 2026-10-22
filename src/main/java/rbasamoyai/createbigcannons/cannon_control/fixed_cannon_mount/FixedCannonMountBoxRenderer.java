package rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount;

import java.util.ArrayList;
import java.util.List;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.Create;
import com.zurrtum.create.client.catnip.outliner.Outliner;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBox;
import com.zurrtum.create.client.foundation.utility.CreateLang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.fabric.client.FixedCannonMountValueBehaviour;

/**
 * Value boxes for the fixed cannon mount's pitch and yaw slots.
 *
 * <p>PORT-NOTE(fabric): Create Fly's {@code ScrollValueRenderer} only draws a block entity's single
 * {@code ScrollValueBehaviour.TYPE} behaviour, so both slots are drawn here, as upstream did.</p>
 */
public class FixedCannonMountBoxRenderer {

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.hitResult instanceof BlockHitResult result) || mc.level == null)
			return;
		ClientLevel level = mc.level;
		BlockPos pos = result.getBlockPos();
		Direction face = result.getDirection();
		if (!(level.getBlockEntity(pos) instanceof FixedCannonMountBlockEntity mount))
			return;

		for (boolean pitch : Iterate.trueAndFalse) {
			FixedCannonMountValueBehaviour behaviour = mount.getBehaviour(pitch ? FixedCannonMountValueBehaviour.PITCH_TYPE : FixedCannonMountValueBehaviour.YAW_TYPE);
			if (behaviour == null)
				continue;
			if (!behaviour.isActive()) {
				Outliner.getInstance().remove(behaviour);
				continue;
			}
			boolean hit = behaviour.testHit(result.getLocation());
			addBox(pos, face, behaviour, hit);
			if (!hit)
				continue;

			List<MutableComponent> tip = new ArrayList<>();
			tip.add(behaviour.label.copy());
			tip.add(CreateLang.translateDirect("gui.value_settings.hold_to_edit"));
			Create.VALUE_SETTINGS_HANDLER.showHoverTip(mc, tip);
		}
	}

	/** Copied from Create Fly's {@code ScrollValueRenderer#addBox}. */
	protected static void addBox(BlockPos pos, Direction face, FixedCannonMountValueBehaviour behaviour, boolean highlight) {
		AABB bb = new AABB(Vec3.ZERO, Vec3.ZERO).inflate(.5f).contract(0, 0, -.5f).move(0, 0, -.125f);
		Component label = behaviour.label;
		ValueBox box = new ValueBox.TextValueBox(label, bb, pos, Component.literal(behaviour.formatValue()));
		box.passive(!highlight).wideOutline();
		Outliner.getInstance().showOutline(behaviour, box.transform(behaviour.getSlotPositioning())).highlightFace(face);
	}

}
