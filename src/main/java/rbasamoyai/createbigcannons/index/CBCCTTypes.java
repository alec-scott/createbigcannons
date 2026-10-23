package rbasamoyai.createbigcannons.index;

import com.zurrtum.create.client.foundation.block.connected.CTType;

import rbasamoyai.createbigcannons.CreateBigCannons;

/**
 * PORT-NOTE(fabric): Create Fly's {@link CTType} is a class with int context flags (no enum/registry). CBC's cannon CT
 * reads a 4x4 sheet; the tile index is resolved by {@code CBCCTSpriteShiftEntry}.
 */
public class CBCCTTypes {

	public static final CTType CANNON = new CTType(CreateBigCannons.resource("cannon"), 1, CTType.VERTICAL) {
		@Override
		public int getTextureIndex(int context) {
			boolean up = (context & UP_FLAG) != 0;
			boolean down = (context & DOWN_FLAG) != 0;
			int i = (up == down ? 1 : 0) + (up ? 0 : 2);
			return i * 4;
		}
	};

	public static int sheetSize(CTType type) {
		return type == CANNON ? 4 : 2;
	}

}
