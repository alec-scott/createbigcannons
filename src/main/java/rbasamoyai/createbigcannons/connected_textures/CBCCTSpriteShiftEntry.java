package rbasamoyai.createbigcannons.connected_textures;

import com.zurrtum.create.client.catnip.render.StitchedSprite;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.CTType;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

/**
 * Connected-texture shift that reads tiles from a single sheet texture.
 *
 * <p>PORT-NOTE(fabric): Create Fly expects CT textures pre-split into numbered tiles ({@code <connected>/<index>}). CBC
 * ships whole sheets, so this entry maps UVs into the sheet itself, as CBC's 1.21.1 entry did; the per-tile sprites the
 * superclass asks for are never used.</p>
 */
public class CBCCTSpriteShiftEntry extends CTSpriteShiftEntry {

	private final int scale;
	private final int sheetSize;
	private final StitchedSprite sheet;

	public CBCCTSpriteShiftEntry(CTType type, int sheetSize, int scale, Identifier blockTexture, Identifier connectedTexture) {
		super(type, blockTexture, connectedTexture);
		this.scale = scale;
		this.sheetSize = sheetSize;
		this.sheet = new StitchedSprite(connectedTexture);
	}

	@Override
	public float getTargetU(float localU, int index) {
		TextureAtlasSprite original = this.getOriginal();
		float uOffset = index % this.sheetSize;
		float u = (localU - original.getU0()) / (original.getU1() - original.getU0());
		return this.sheet.get().getU((u * this.scale + uOffset) / this.sheetSize);
	}

	@Override
	public float getTargetV(float localV, int index) {
		TextureAtlasSprite original = this.getOriginal();
		float vOffset = index / this.sheetSize;
		float v = (localV - original.getV0()) / (original.getV1() - original.getV0());
		return this.sheet.get().getV((v * this.scale + vOffset) / this.sheetSize);
	}

}
