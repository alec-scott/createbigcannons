package rbasamoyai.createbigcannons.equipment.gas_mask;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;



import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import rbasamoyai.createbigcannons.CreateBigCannons;

public class GasMaskOverlay {

	public static final Identifier GAS_MASK_OVERLAY = CreateBigCannons.resource("textures/misc/gas_mask_overlay.png");

	public static void renderOverlay(GuiGraphicsExtractor graphics, DeltaTracker tracker) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.options.getCameraType() != CameraType.FIRST_PERSON
			|| !GasMaskItem.canShowGasMaskOverlay(minecraft.player))
			return;
		// PORT-NOTE(mc26.1): as Gui#extractTextureOverlay; GUI pipelines handle blending, so no GL state juggling.
		graphics.blit(RenderPipelines.GUI_TEXTURED, GAS_MASK_OVERLAY, 0, 0, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(),
			graphics.guiWidth(), graphics.guiHeight(), ARGB.white(1.0f));
	}

}
