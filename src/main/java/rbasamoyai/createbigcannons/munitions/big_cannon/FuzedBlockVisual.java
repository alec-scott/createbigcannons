package rbasamoyai.createbigcannons.munitions.big_cannon;

import java.util.function.Consumer;

import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.instance.Instancer;
import com.zurrtum.create.client.flywheel.api.visual.DynamicVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.instance.InstanceTypes;
import com.zurrtum.create.client.flywheel.lib.instance.OrientedInstance;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.zurrtum.create.client.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;

public class FuzedBlockVisual extends AbstractBlockEntityVisual<FuzedBlockEntity> implements SimpleDynamicVisual {

	private final OrientedInstance fuze;
	private boolean oldBaseFuze;

	public FuzedBlockVisual(VisualizationContext ctx, FuzedBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
        Direction facing = this.blockState.getValue(BlockStateProperties.FACING);
        this.oldBaseFuze = this.isBaseFuze();
        if (this.oldBaseFuze)
            facing = facing.getOpposite();
        this.fuze = this.fuzeProvider(facing).createInstance();
        this.fuze.position(this.getVisualPosition());
	}

    protected void refreshFuze() {
        Direction facing = this.blockState.getValue(BlockStateProperties.FACING);
        this.oldBaseFuze = this.isBaseFuze();
        if (this.oldBaseFuze)
            facing = facing.getOpposite();
        this.fuzeProvider(facing).stealInstance(this.fuze);
    }

    protected Instancer<OrientedInstance> fuzeProvider(Direction facing) {
        return instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(CBCBlockPartials.FUZE, facing));
    }

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
        this.fuze.setVisible(this.blockEntity.hasFuze());
        if (this.oldBaseFuze != this.isBaseFuze())
            this.refreshFuze();
	}

	@Override
	public void _delete() {
		this.fuze.delete();
	}

	@Override
	public void updateLight(float partialTick) {
        this.relight(this.fuze);
	}

	private boolean isBaseFuze() {
		return this.blockState.getBlock() instanceof FuzedProjectileBlock<?, ?> fuzed && fuzed.isBaseFuze();
	}

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(this.fuze);
    }

}
