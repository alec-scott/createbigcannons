package rbasamoyai.createbigcannons.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.client.TrinketRenderer;
import eu.pb4.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.createbigcannons.index.CBCItems;

// Adapted from Create's GoggleTrinketRenderer class --ritchie
// 26.1 port: follows Create Fly's 26.1 GoggleTrinketRenderer (submit-based rendering).
public class GasMaskTrinketRenderer implements TrinketRenderer {

	public static void register() {
		TrinketRendererRegistry.registerRenderer(CBCItems.GAS_MASK.get(), new GasMaskTrinketRenderer());
	}

	@Override
	public void submit(ItemStack stack, TrinketSlotAccess slotReference, EntityModel<? extends LivingEntityRenderState> contextModel,
					   PoseStack matrices, SubmitNodeCollector queue, int light, LivingEntityRenderState state,
					   float limbAngle, float limbDistance) {
		if (!CBCItems.GAS_MASK.isIn(stack) || !(contextModel instanceof PlayerModel playerModel))
			return;

		// Translate and rotate with our head
		matrices.pushPose();
		playerModel.root().translateAndRotate(matrices);
		playerModel.getHead().translateAndRotate(matrices);

		// Translate and scale to our head
		matrices.translate(0.0F, -0.25F, 0.0F);
		matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
		matrices.scale(0.625F, -0.625F, -0.625F);

		// Render
		ItemStackRenderState item = new ItemStackRenderState();
		Minecraft mc = Minecraft.getInstance();
		mc.getItemModelResolver().updateForTopItem(item, stack, ItemDisplayContext.HEAD, mc.level, null, 0);
		item.submit(matrices, queue, light, OverlayTexture.NO_OVERLAY, 0);
		matrices.popPose();
	}

}
