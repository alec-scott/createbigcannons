package rbasamoyai.createbigcannons.connected_textures;

import java.util.HashMap;
import java.util.Map;

import com.zurrtum.create.client.foundation.block.connected.CTType;

import net.minecraft.resources.Identifier;
import rbasamoyai.createbigcannons.index.CBCCTTypes;

public class CBCCTSpriteShifter {

	private static final Map<String, CBCCTSpriteShiftEntry> ENTRY_CACHE = new HashMap<>();

	public static CBCCTSpriteShiftEntry getCT(CTType type, int scale, Identifier blockTexture, Identifier connectedTexture) {
		String key = blockTexture + "->" + connectedTexture + "+" + type.getId();
		return ENTRY_CACHE.computeIfAbsent(key, k -> new CBCCTSpriteShiftEntry(type, CBCCTTypes.sheetSize(type), scale, blockTexture, connectedTexture));
	}

}
