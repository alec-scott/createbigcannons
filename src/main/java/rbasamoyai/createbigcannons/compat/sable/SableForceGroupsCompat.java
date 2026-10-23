package rbasamoyai.createbigcannons.compat.sable;

import java.util.function.Supplier;

import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import rbasamoyai.createbigcannons.CreateBigCannons;

/**
 * PORT-NOTE(fabric): NeoForge {@code DeferredRegister} → direct registration into Sable's vanilla force-group registry,
 * done in {@link #init()} from {@link SableCompat#onModCtor()}.
 */
public class SableForceGroupsCompat {

    private static ForceGroup recoil;
    private static ForceGroup impact;

    public static final Supplier<ForceGroup> RECOIL = () -> recoil;
    public static final Supplier<ForceGroup> IMPACT = () -> impact;

    public static void init() {
        recoil = Registry.register(ForceGroups.REGISTRY, CreateBigCannons.resource("recoil"), new ForceGroup(
            Component.translatable("force_group.createbigcannons.recoil_force"), null, 0x504550, true));
        impact = Registry.register(ForceGroups.REGISTRY, CreateBigCannons.resource("impact"), new ForceGroup(
            Component.translatable("force_group.createbigcannons.impact_force"), null, 0x808080, true));
    }
}
