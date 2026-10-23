package rbasamoyai.createbigcannons.crafting.builtup;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class LayeredCannonBlockEntityRenderer extends CBCSafeBlockEntityRenderer<LayeredBigCannonBlockEntity> {

	public LayeredCannonBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(LayeredBigCannonBlockEntity be, float partialTicks, PoseStack ms, Parts buffer, int light, int overlay) {
		if (be.getLayers().size() < 2) return;
		for (Block block : be.getLayers().values()) {
			BlockState state = block.defaultBlockState();
			if (state.getRenderShape() == RenderShape.MODEL) {
				ms.pushPose();

				if (state.hasProperty(BlockStateProperties.FACING) && be.getBlockState().hasProperty(BlockStateProperties.FACING)) {
					state = state.setValue(BlockStateProperties.FACING, be.getBlockState().getValue(BlockStateProperties.FACING));
				}

				buffer.block(ms, state, light);

				ms.popPose();
			}
		}
	}

	@Override
	public boolean shouldRender(LayeredBigCannonBlockEntity be, Vec3 vec) {
		return be.getLayers().size() > 1 && super.shouldRender(be, vec);
	}

}
