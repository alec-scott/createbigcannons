package rbasamoyai.createbigcannons.crafting.casting;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.mojang.blaze3d.vertex.PoseStack;

import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;

public class FinishedCannonCastBlockEntityRenderer extends CBCSafeBlockEntityRenderer<FinishedCannonCastBlockEntity> {

	public FinishedCannonCastBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super();
	}

	@Override
	protected void renderSafe(FinishedCannonCastBlockEntity te, float partialTicks, PoseStack ms, Parts buffer, int light, int overlay) {
		if (!te.isCentralBlock()) return;
		ms.pushPose();

		BlockState state = te.getBlockState();

		SuperByteBuffer castRender = CachedBuffers.partial(CBCBlockPartials.cannonCastFor(te.getRenderedShape()), state);
		buffer.add(ms, castRender
			.translate(1, 0, 1)
			.light(light));

		ms.popPose();
	}

}
