package rbasamoyai.createbigcannons.base.goggles;


import java.util.List;

import net.minecraft.network.chat.Component;

/**
 * Goggle/hover overlay lines for a CBC block entity.
 *
 * <p>PORT-NOTE(fabric): replaces implementing Create's {@code IHaveGoggleInformation}/{@code IHaveHoveringInformation} on
 * block entities. In Create Fly those are client-only and come from per-type tooltip behaviours, so a block entity
 * implementing them can't load on a dedicated server. {@code mixin.client.GoggleOverlayRendererMixin} reads this
 * interface on the client and adds these lines after any Create (e.g. kinetic stress) information.</p>
 */
public interface CBCGoggleInfo {

	default boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;
	}

	default boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;
	}

}
