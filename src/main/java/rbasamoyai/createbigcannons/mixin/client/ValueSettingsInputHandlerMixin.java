package rbasamoyai.createbigcannons.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.Create;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueSettingsInputHandler;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;
import rbasamoyai.createbigcannons.fabric.client.FixedCannonMountValueBehaviour;

/**
 * PORT-NOTE(fabric): replaces {@code ValueSettingsClientMixin}. Create Fly's input handler only looks up a block
 * entity's single {@code ScrollValueBehaviour.TYPE} behaviour, so the fixed cannon mount's pitch and yaw slots are tried
 * here first. The rest of the flow (hold to edit, the value screen, the server packet) is Create Fly's own.
 */
@Mixin(ValueSettingsInputHandler.class)
public abstract class ValueSettingsInputHandlerMixin {

	@Invoker("handleInteraction")
	private static InteractionResult createbigcannons$handleInteraction(BlockEntityBehaviour<?> behaviour,
																		BehaviourType<? extends BlockEntityBehaviour<?>> type,
																		LocalPlayer player, InteractionHand hand, BlockHitResult ray,
																		ItemStack stack, BlockPos pos, SmartBlockEntity sbe) {
		throw new AssertionError();
	}

	@Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
	private static void createbigcannons$fixedCannonMountSlots(Level world, LocalPlayer player, InteractionHand hand, BlockHitResult ray,
															   CallbackInfoReturnable<InteractionResult> cir) {
		if (!ValueSettingsInputHandler.canInteract(player)
			|| !(world.getBlockEntity(ray.getBlockPos()) instanceof FixedCannonMountBlockEntity mount))
			return;
		if (Create.VALUE_SETTINGS_HANDLER.cancelIfWarmupAlreadyStarted(ray.getBlockPos())) {
			cir.setReturnValue(InteractionResult.FAIL);
			return;
		}
		ItemStack stack = player.getMainHandItem();
		for (BehaviourType<FixedCannonMountValueBehaviour> type : new BehaviourType[] {
			FixedCannonMountValueBehaviour.PITCH_TYPE, FixedCannonMountValueBehaviour.YAW_TYPE }) {
			FixedCannonMountValueBehaviour behaviour = mount.getBehaviour(type);
			if (behaviour == null)
				continue;
			InteractionResult result = createbigcannons$handleInteraction(behaviour, type, player, hand, ray, stack, ray.getBlockPos(), mount);
			if (result != null) {
				cir.setReturnValue(result);
				return;
			}
		}
		cir.setReturnValue(null);
	}

}
