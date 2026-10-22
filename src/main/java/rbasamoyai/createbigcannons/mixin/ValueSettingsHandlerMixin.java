package rbasamoyai.createbigcannons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.zurrtum.create.AllHandle;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettingsHandleBehaviour;
import com.zurrtum.create.infrastructure.packet.c2s.ValueSettingsPacket;

import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity.FixedCannonMountScrollValueBehaviour;

/**
 * PORT-NOTE(fabric): Create Fly hands value-settings packets only to a block entity's
 * {@code ServerScrollValueBehaviour.TYPE} behaviour. The fixed cannon mount has two scroll slots, so packets carrying the
 * yaw slot's net id are redirected to the yaw behaviour.
 */
@Mixin(AllHandle.class)
public class ValueSettingsHandlerMixin {

	@ModifyVariable(method = "handleValueSettings", at = @At("HEAD"), argsOnly = true)
	private static ValueSettingsHandleBehaviour createbigcannons$routeFixedMountYaw(ValueSettingsHandleBehaviour handle, ServerPlayer player,
																					ValueSettingsHandleBehaviour original, ValueSettingsPacket packet) {
		if (handle instanceof FixedCannonMountScrollValueBehaviour pitchSlot
			&& packet.behaviourIndex() == FixedCannonMountScrollValueBehaviour.YAW_NET_ID) {
			FixedCannonMountScrollValueBehaviour yaw = pitchSlot.blockEntity.getBehaviour(FixedCannonMountScrollValueBehaviour.YAW_TYPE);
			if (yaw != null)
				return yaw;
		}
		return handle;
	}

}
