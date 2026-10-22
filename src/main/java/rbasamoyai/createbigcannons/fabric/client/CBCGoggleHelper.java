package rbasamoyai.createbigcannons.fabric.client;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;

import net.minecraft.network.chat.Component;

/** Client-side access to Create Fly's goggle helpers (formerly inherited from {@code IHaveGoggleInformation}). */
public final class CBCGoggleHelper {
	private static final IHaveGoggleInformation HELPER = new IHaveGoggleInformation() {};

	private CBCGoggleHelper() {
	}

	public static boolean containedFluidTooltip(List<Component> tooltip, boolean isPlayerSneaking, @Nullable FluidInventory handler) {
		return HELPER.containedFluidTooltip(tooltip, isPlayerSneaking, handler);
	}
}
