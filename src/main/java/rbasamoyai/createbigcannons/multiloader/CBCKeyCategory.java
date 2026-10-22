package rbasamoyai.createbigcannons.multiloader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import rbasamoyai.createbigcannons.CreateBigCannons;

/** PORT-NOTE(mc26.1): key-mapping categories are registered objects, not translation-key strings. */
@Environment(EnvType.CLIENT)
public final class CBCKeyCategory {
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(CreateBigCannons.resource("category"));

	private CBCKeyCategory() {
	}
}
