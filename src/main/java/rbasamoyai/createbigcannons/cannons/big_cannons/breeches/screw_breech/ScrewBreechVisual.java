package rbasamoyai.createbigcannons.cannons.big_cannons.breeches.screw_breech;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.math.Axis;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.OrientedRotatingVisual;

import com.zurrtum.create.client.flywheel.api.visual.DynamicVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.instance.InstanceTypes;
import com.zurrtum.create.client.flywheel.lib.instance.OrientedInstance;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.flywheel.lib.visual.SimpleDynamicVisual;
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.createbigcannons.CBCClientCommon;

public class ScrewBreechVisual extends OrientedRotatingVisual<ScrewBreechBlockEntity> implements SimpleDynamicVisual {

	private final ScrewBreechBlockEntity breech;
	private final OrientedInstance screwLock;
	private final Direction facing;

	protected ScrewBreechVisual(VisualizationContext ctx, ScrewBreechBlockEntity tile, float partialTick, Direction to) {
		super(ctx, tile, partialTick, Direction.SOUTH, to, Models.partial(AllPartialModels.SHAFT_HALF));
		this.breech = tile;
        this.facing = to;
        this.screwLock = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(CBCClientCommon.getScrewBreechForState(this.blockState),
            this.facing)).createInstance();
        this.transformModels(partialTick);
	}

    public static SimpleBlockEntityVisualizer.Factory<ScrewBreechBlockEntity> factory() {
        return (ctx, be, partialTick) -> {
            Direction to = be.getBlockState().getValue(BlockStateProperties.FACING);
            return new ScrewBreechVisual(ctx, be, partialTick, to);
        };
    }

	private void transformModels(float partialTick) {
		float renderedScrewLockOffset = this.breech.getRenderedBlockOffset(partialTick);
		float heightOffset = renderedScrewLockOffset * 0.25f;
		float rotationOffset = renderedScrewLockOffset * 360.0f;
		Vector3f normal = this.facing.step();
		Vector3f height = new Vector3f(normal);
		height.mul(heightOffset);

		Quaternionf q = Axis.of(normal).rotationDegrees(rotationOffset);

		this.screwLock.position(this.getVisualPosition()).translatePosition(height.x(), height.y(), height.z()).rotation(q).setChanged();
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		this.transformModels(ctx.partialTick());
	}

	@Override
	public void _delete() {
		super._delete();
		this.screwLock.delete();
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		this.relight(this.pos, this.screwLock);
	}
}
