package rbasamoyai.createbigcannons.munitions.big_cannon;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer.Parts;

import rbasamoyai.createbigcannons.fabric.client.render.CBCSafeBlockEntityRenderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.mojang.blaze3d.vertex.PoseStack;

import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;

public class FuzedBlockEntityRenderer extends CBCSafeBlockEntityRenderer<FuzedBlockEntity> {

	public FuzedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super();
	}

	@Override
	protected void renderSafe(FuzedBlockEntity blockEntity, float partialTicks, PoseStack posestack, Parts buffers, int packedLight, int packedOverlay) {
		if (VisualizationManager.supportsVisualization(blockEntity.getLevel())) return;

		BlockState state = blockEntity.getBlockState();
		Direction facing = state.getValue(BlockStateProperties.FACING);
		if (state.getBlock() instanceof FuzedProjectileBlock<?, ?> fuzed && fuzed.isBaseFuze())
			facing = facing.getOpposite();
		if (blockEntity.hasFuze()) {
            SuperByteBuffer fuzeRender = CachedBuffers.partialFacing(CBCBlockPartials.FUZE, blockEntity.getBlockState(), facing);
			buffers.add(posestack, fuzeRender
                .light(packedLight));
		}
	}

}
