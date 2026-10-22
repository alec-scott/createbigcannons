package rbasamoyai.createbigcannons.munitions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.CBCCompatTransformers;
import rbasamoyai.createbigcannons.events.ProjectileDamageEvent;

public class ProjectileDamageHooks {
    private ProjectileDamageHooks(){
    }

    public static boolean canDamageTerrain(Level level, BlockPos pos){
        if(level.isClientSide()) {
            return true;
        }
        BlockPos realPos = CBCCompatTransformers.transformBlockPos(level, pos);
        return ProjectileDamageEvent.EVENT.invoker().canDamageTerrain(level, realPos);
    }
}
