package rbasamoyai.createbigcannons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.zurrtum.create.content.kinetics.deployer.DeployerBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.CBCCommonEvents;

/**
 * PORT-NOTE(fabric): NeoForge Create posted a deployer recipe search event that CBC added its munition assembly recipes
 * to. Create Fly has no such event, so they are offered here when Create Fly finds no recipe of its own.
 */
@Mixin(DeployerBlockEntity.class)
public abstract class DeployerBlockEntityMixin extends SmartBlockEntity {

	DeployerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
	private void createbigcannons$getRecipe(ItemStack stack, CallbackInfoReturnable<Recipe<? extends RecipeInput>> cir) {
		DeployerBlockEntity self = (DeployerBlockEntity) (Object) this;
		if (cir.getReturnValue() != null || self.getPlayer() == null || this.level == null)
			return;
		ItemStack held = self.getPlayer().cast().getMainHandItem();
		Recipe<? extends RecipeInput> recipe = CBCCommonEvents.getDeployerRecipe(this.level, stack, held);
		if (recipe != null)
			cir.setReturnValue(recipe);
	}

}
