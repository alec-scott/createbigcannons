package rbasamoyai.createbigcannons.cannons.autocannon.material;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.Identifier;

public record AutocannonMaterial(Identifier name, AutocannonMaterialProperties defaultProperties) {

	public AutocannonMaterialProperties properties() {
		AutocannonMaterialProperties handlerProperties = AutocannonMaterialPropertiesHandler.getMaterial(this);
		return handlerProperties == null ? this.defaultProperties : handlerProperties;
	}

    private static final Map<Identifier, AutocannonMaterial> CANNON_MATERIALS = new HashMap<>();

    public static AutocannonMaterial register(Identifier loc, AutocannonMaterialProperties defaultProperties) {
        AutocannonMaterial material = new AutocannonMaterial(loc, defaultProperties);
        CANNON_MATERIALS.put(material.name(), material);
        return material;
    }

    public static AutocannonMaterial fromName(Identifier loc) {
		if (!CANNON_MATERIALS.containsKey(loc)) throw new IllegalArgumentException("No autocannon material '" + loc + "' registered");
        return CANNON_MATERIALS.get(loc);
    }

	public static AutocannonMaterial fromNameOrNull(Identifier loc) { return CANNON_MATERIALS.get(loc); }

}
