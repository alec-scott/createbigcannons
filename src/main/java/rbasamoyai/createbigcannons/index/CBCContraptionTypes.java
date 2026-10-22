package rbasamoyai.createbigcannons.index;


import java.util.function.Supplier;

import com.zurrtum.create.api.contraption.ContraptionType;
import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.content.contraptions.Contraption;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.cannon_control.contraption.MountedAutocannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.MountedBigCannonContraption;
import rbasamoyai.createbigcannons.cannon_loading.CannonLoadingContraption;
import rbasamoyai.createbigcannons.crafting.boring.CannonDrillingContraption;
import rbasamoyai.createbigcannons.crafting.builtup.CannonBuildingContraption;

public class CBCContraptionTypes {

	public static final Holder.Reference<ContraptionType>
		CANNON_LOADER = register("cannon_loader", CannonLoadingContraption::new),
        MOUNTED_CANNON = register("mounted_cannon", MountedBigCannonContraption::new),
        MOUNTED_AUTOCANNON = register("mounted_autocannon", MountedAutocannonContraption::new),
        CANNON_DRILL = register("cannon_drill", CannonDrillingContraption::new),
        CANNON_BUILDER = register("cannon_builder", CannonBuildingContraption::new);

    private static Holder.Reference<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);

        // PORT-NOTE(fabric): Create Fly's contraption types carry an intrusive holder; no legacy-name table.
        Registry.register(CreateRegistries.CONTRAPTION_TYPE, CreateBigCannons.resource(name), type);
        return type.holder;
    }

    public static void init() {
    }

}
