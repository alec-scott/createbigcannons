package rbasamoyai.createbigcannons.cannons.big_cannons.material;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.Identifier;

public record BigCannonMaterial(Identifier name, BigCannonMaterialProperties defaultProperties) {

	public BigCannonMaterialProperties properties() {
		BigCannonMaterialProperties custom = BigCannonMaterialPropertiesHandler.getMaterial(this);
		return custom == null ? this.defaultProperties : custom;
	}

	private static final Map<Identifier, BigCannonMaterial> CANNON_MATERIALS = new HashMap<>();

	public static BigCannonMaterial register(Identifier loc, BigCannonMaterialProperties defaultProperties) {
		BigCannonMaterial material = new BigCannonMaterial(loc, defaultProperties);
		CANNON_MATERIALS.put(material.name(), material);
		return material;
	}

	public static BigCannonMaterial fromName(Identifier loc) {
		if (!CANNON_MATERIALS.containsKey(loc)) throw new IllegalArgumentException("No big cannon material '" + loc + "' registered");
		return CANNON_MATERIALS.get(loc);
	}

	public static BigCannonMaterial fromNameOrNull(Identifier loc) { return CANNON_MATERIALS.get(loc); }

}
