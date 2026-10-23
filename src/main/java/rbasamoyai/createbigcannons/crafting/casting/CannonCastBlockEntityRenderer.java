package rbasamoyai.createbigcannons.crafting.casting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.FluidRenderHelper;
import com.zurrtum.create.client.catnip.render.FluidRenderHelper.FluidRenderState;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

/**
 * PORT-NOTE(mc26.1): the fluid fill uses Create Fly's {@link FluidRenderHelper} render state, and the casting preview is
 * drawn as the result block's model with a translucent block render type, tinted to the casting progress (it used to
 * re-emit the baked quads into the translucent item sheet).
 */
public class CannonCastBlockEntityRenderer extends AbstractCannonCastBlockEntityRenderer {

	public CannonCastBlockEntityRenderer(BlockEntityRendererProvider.Context context) { super(context); }

	@Override
    protected void renderFluidBox(AbstractCannonCastBlockEntity cast, float width, float height, Parts buffers, PoseStack stack, int light) {
        if (!(cast instanceof CannonCastBlockEntity castc))
            return;
        FluidStack fstack = castc.fluid.getFluid();
        if (fstack.isEmpty())
            return;
        FluidRenderState fluid = FluidRenderHelper.extractFluidRenderState(cast.getLevel() instanceof net.minecraft.client.renderer.block.BlockAndTintGetter getter ? getter : null, cast.getBlockPos(),
            Minecraft.getInstance().getModelManager().getFluidStateModelSet(), fstack.getFluid(), fstack.getComponentChanges(),
            0f, 0f, 0f, width, height, width, light, false, true);
        buffers.custom(stack, fluid.layer(), fluid::render);
    }

	@Override
	protected void renderPreview(PoseStack ms, float alpha, Parts buffers, BlockState state, int light, BlockPos pos) {
		int a = Math.round(Math.max(0, Math.min(1, alpha)) * 255);
		buffers.add(ms, CachedBuffers.block(state).light(light).color(255, 255, 255, a), RenderTypes.translucentMovingBlock());
	}

}
