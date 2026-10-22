package rbasamoyai.createbigcannons.multiloader;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

/** PORT-NOTE(fabric): Fabric transfer API instead of NeoForge fluid capabilities; amounts in CBC units (millibuckets). */
public class PonderPlatform {

	public static void drain(BlockEntity be, int count, @Nullable Direction dir) {
		Storage<FluidVariant> storage = find(be, dir);
		if (storage == null) return;
		long amount = (long) (count * IndexPlatform.getFluidConversionFactor());
		try (Transaction tx = Transaction.openOuter()) {
			for (StorageView<FluidVariant> view : storage) {
				if (view.isResourceBlank()) continue;
				amount -= view.extract(view.getResource(), amount, tx);
				if (amount <= 0) break;
			}
			tx.commit();
		}
	}

	public static void fillWith(BlockEntity be, Fluid fluid, int count, @Nullable Direction dir) {
		Storage<FluidVariant> storage = find(be, dir);
		if (storage == null) return;
		try (Transaction tx = Transaction.openOuter()) {
			storage.insert(FluidVariant.of(fluid), (long) (count * IndexPlatform.getFluidConversionFactor()), tx);
			tx.commit();
		}
	}

	@Nullable
	private static Storage<FluidVariant> find(BlockEntity be, @Nullable Direction dir) {
		return be.getLevel() == null ? null : FluidStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, dir);
	}

}
