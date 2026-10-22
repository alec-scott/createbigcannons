package rbasamoyai.createbigcannons.munitions.config;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public abstract class EntityPropertiesTypeHandler<PROPERTIES> extends PropertiesTypeHandler<EntityType<?>, PROPERTIES> {

	@NotNull public final PROPERTIES getPropertiesOf(Entity entity) { return this.getPropertiesOf(entity.getType()); }

}
