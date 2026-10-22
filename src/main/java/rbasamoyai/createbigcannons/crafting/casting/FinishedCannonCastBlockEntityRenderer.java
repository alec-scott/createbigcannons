package rbasamoyai.createbigcannons.crafting.casting;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;

public class FinishedCannonCastBlockEntityRenderer extends SafeBlockEntityRenderer<FinishedCannonCastBlockEntity> {

	public FinishedCannonCastBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super();
	}

	@Override
	protected void renderSafe(FinishedCannonCastBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		if (!te.isCentralBlock()) return;
		ms.pushPose();

		BlockState state = te.getBlockState();

		SuperByteBuffer castRender = CachedBuffers.partial(CBCBlockPartials.cannonCastFor(te.getRenderedShape()), state);
		castRender
			.translate(1, 0, 1)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderTypes.solid()));

		ms.popPose();
	}

}
