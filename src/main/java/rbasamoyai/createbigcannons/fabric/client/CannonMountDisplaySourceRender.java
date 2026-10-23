package rbasamoyai.createbigcannons.fabric.client;

import java.util.Arrays;

import com.zurrtum.create.api.behaviour.display.DisplaySource;
import com.zurrtum.create.client.content.redstone.displayLink.source.SingleLineDisplaySourceRender;
import com.zurrtum.create.client.foundation.gui.ModularGuiLineBuilder;
import com.zurrtum.create.content.redstone.displayLink.DisplayLinkContext;

import net.minecraft.network.chat.Component;
import rbasamoyai.createbigcannons.CreateBigCannons;

/**
 * Display link options for the cannon mount source (yaw or pitch).
 *
 * <p>PORT-NOTE(fabric): replaces {@code CannonMountDisplaySourceMixin}; Create Fly attaches display-source UI as a
 * client-side {@code DisplaySourceRender}.</p>
 */
public class CannonMountDisplaySourceRender extends SingleLineDisplaySourceRender {

	@Override
	public void initConfigurationWidgets(DisplaySource source, DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
		super.initConfigurationWidgets(source, context, builder, isFirstLine);
		if (isFirstLine)
			return;
		builder.addSelectionScrollInput(0, 120,
			(si, l) -> si.forOptions(Arrays.asList(Component.translatable(CreateBigCannons.MOD_ID + ".display_source.cannon_mount.yaw"),
				Component.translatable(CreateBigCannons.MOD_ID + ".display_source.cannon_mount.pitch")))
				.titled(Component.translatable(CreateBigCannons.MOD_ID + ".display_source.cannon_mount.display_rotation_axis")),
			"Mode");
	}

}
