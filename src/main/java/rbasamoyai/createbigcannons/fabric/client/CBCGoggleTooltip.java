package rbasamoyai.createbigcannons.fabric.client;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.zurrtum.create.client.content.contraptions.IDisplayAssemblyExceptions;
import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.api.goggles.IHaveHoveringInformation;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;
import com.zurrtum.create.content.contraptions.AssemblyException;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.network.chat.Component;
import rbasamoyai.createbigcannons.base.goggles.CBCAssemblyExceptionHolder;
import rbasamoyai.createbigcannons.base.goggles.CBCGoggleInfo;

/**
 * Combines a block entity's Create tooltip behaviour (if any) with its {@link CBCGoggleInfo} lines, for the goggle overlay.
 */
public class CBCGoggleTooltip extends TooltipBehaviour<SmartBlockEntity> implements IHaveGoggleInformation, IHaveHoveringInformation,
	IDisplayAssemblyExceptions {

	@Nullable private final TooltipBehaviour<?> base;
	private final CBCGoggleInfo info;

	public CBCGoggleTooltip(@Nullable TooltipBehaviour<?> base, CBCGoggleInfo info) {
		super(null);
		this.base = base;
		this.info = info;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean added = this.base instanceof IHaveGoggleInformation goggles && goggles.addToGoggleTooltip(tooltip, isPlayerSneaking);
		return this.info.addToGoggleTooltip(tooltip, isPlayerSneaking) | added;
	}

	@Override
	public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean added = this.base instanceof IHaveHoveringInformation hovering && hovering.addToTooltip(tooltip, isPlayerSneaking);
		return this.info.addToTooltip(tooltip, isPlayerSneaking) | added;
	}


	@Nullable
	@Override
	public AssemblyException getLastAssemblyException() {
		return this.info instanceof CBCAssemblyExceptionHolder holder ? holder.getLastAssemblyException() : null;
	}

}
