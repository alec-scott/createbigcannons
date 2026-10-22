package rbasamoyai.createbigcannons.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import rbasamoyai.createbigcannons.remix.CustomExplosion;

/**
 * PORT-NOTE(mc26.1): replaces {@code ExplosionMixin}. Explosion logic moved to {@link ServerExplosion}, and
 * {@code explode()} now destroys blocks itself, so terrain protection skips the block search instead of clearing the
 * old {@code toBlow} list afterwards.
 */
@Mixin(ServerExplosion.class)
public class ServerExplosionMixin {

	@Shadow @Final private ServerLevel level;

	@WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;calculateExplodedPositions()Ljava/util/List;"))
	private List<BlockPos> createbigcannons$explode$protectTerrain(ServerExplosion instance, Operation<List<BlockPos>> original) {
		if ((Object) this instanceof CustomExplosion customExplosion && !customExplosion.canDamageTerrain())
			return new ArrayList<>();
		return original.call(instance);
	}

	@WrapOperation(method = "calculateExplodedPositions",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ExplosionDamageCalculator;getBlockExplosionResistance(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Ljava/util/Optional;"))
	private Optional<Float> createbigcannons$calculateExplodedPositions$editBlock(ExplosionDamageCalculator instance, Explosion explosion, BlockGetter reader,
																				  BlockPos pos, BlockState state, FluidState fluid, Operation<Optional<Float>> original,
																				  @Local(ordinal = 0) float power) {
		if ((Object) this instanceof CustomExplosion customExplosion && customExplosion.canDamageTerrain())
			customExplosion.editBlock(this.level, pos, state, fluid, power);
		return original.call(instance, explosion, reader, pos, state, fluid);
	}

	@WrapOperation(method = "hurtEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/ServerExplosion;radius:F", opcode = Opcodes.GETFIELD))
	private float createbigcannons$hurtEntities$setEntityRadius(ServerExplosion instance, Operation<Float> original) {
		if ((Object) this instanceof CustomExplosion customExplosion)
			return customExplosion.getEntityRadius();
		return original.call(instance);
	}

}
