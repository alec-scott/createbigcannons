package rbasamoyai.createbigcannons.multiloader;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;

/**
 * PORT-NOTE(fabric): the port Registrate's {@code EntityBuilder} hands out a {@link FabricEntityTypeBuilder}, as on
 * upstream's 1.20.1 Fabric build, so both builders are supported. Fabric's builder takes size and eye height as one
 * {@link EntityDimensions}, so they are tracked here and reapplied together.
 */
public class EntityTypeConfigurator {

    public static EntityTypeConfigurator of(Object builder) {
        if (builder instanceof EntityType.Builder<?> builderc)
            return new EntityTypeConfigurator(builderc, null);
        if (builder instanceof FabricEntityTypeBuilder<?> fabricBuilder)
            return new EntityTypeConfigurator(null, fabricBuilder);
        throw new IllegalStateException("'builder' is not an EntityType.Builder or FabricEntityTypeBuilder");
    }

    private final EntityType.Builder<?> builder;
    private final FabricEntityTypeBuilder<?> fabricBuilder;
    private EntityDimensions dimensions = EntityDimensions.scalable(0.6f, 1.8f);

    protected EntityTypeConfigurator(EntityType.Builder<?> builder, FabricEntityTypeBuilder<?> fabricBuilder) {
        this.builder = builder;
        this.fabricBuilder = fabricBuilder;
    }

    public EntityTypeConfigurator size(float width, float height) {
        if (this.builder != null) {
            this.builder.sized(width, height);
        } else {
            this.dimensions = EntityDimensions.scalable(width, height);
            this.fabricBuilder.dimensions(this.dimensions);
        }
        return this;
    }

    public EntityTypeConfigurator eyeHeight(float eyeHeight) {
        if (this.builder != null) {
            this.builder.eyeHeight(eyeHeight);
        } else {
            this.dimensions = this.dimensions.withEyeHeight(eyeHeight);
            this.fabricBuilder.dimensions(this.dimensions);
        }
        return this;
    }

    public EntityTypeConfigurator fireImmune() {
        if (this.builder != null) this.builder.fireImmune();
        else this.fabricBuilder.fireImmune();
        return this;
    }

    public EntityTypeConfigurator trackingRange(int range) {
        if (this.builder != null) this.builder.clientTrackingRange(range);
        else this.fabricBuilder.trackRangeChunks(range);
        return this;
    }

    public EntityTypeConfigurator updateInterval(int interval) {
        if (this.builder != null) this.builder.updateInterval(interval);
        else this.fabricBuilder.trackedUpdateRate(interval);
        return this;
    }

    public EntityTypeConfigurator updateVelocity(boolean update) {
        if (this.builder != null) this.builder.alwaysUpdateVelocity(update); // Fabric API builder extension
        else this.fabricBuilder.forceTrackedVelocityUpdates(update);
        return this;
    }

}
