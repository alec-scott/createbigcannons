package rbasamoyai.createbigcannons.fabric.client;

import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.zurrtum.create.client.foundation.item.KineticStats;
import com.zurrtum.create.client.foundation.item.TooltipModifier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.port.registrate.util.entry.RegistryEntry;

/**
 * Item description tooltips (the "hold shift" summaries).
 *
 * <p>PORT-NOTE(fabric): upstream set {@code REGISTRATE.setTooltipModifierFactory(...)} in common code. Create Fly's
 * tooltip classes are client-only, so every CBC item is registered here from the client entrypoint instead.</p>
 */
public class CBCItemTooltips {

	public static void register() {
		for (RegistryEntry<Item, Item> entry : CreateBigCannons.REGISTRATE.<Item, Item>getAll(Registries.ITEM)) {
			Item item = entry.get();
			if (TooltipModifier.REGISTRY.get(item) != null)
				continue;
			TooltipModifier.REGISTRY.register(item, new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item))));
		}
	}

}
