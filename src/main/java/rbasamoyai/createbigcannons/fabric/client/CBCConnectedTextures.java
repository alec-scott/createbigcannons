package rbasamoyai.createbigcannons.fabric.client;

import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createbigcannons.cannons.big_cannons.BuiltUpCannonCTBehavior;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechCTBehavior;
import rbasamoyai.createbigcannons.index.CBCSpriteShifts;
import rbasamoyai.createbigcannons.index.CBCBlocks;

/**
 * Connected-texture behaviours for CBC blocks.
 *
 * <p>PORT-NOTE(fabric): upstream attached these in {@code CBCBlocks} with Registrate's
 * {@code onRegister(CreateRegistrate.connectedTextures(...))}. Create Fly's CT classes are client-only, so they are bound
 * here, from the client entry point, through Create Fly's {@link AllModels} block-model hook.</p>
 */
@Environment(EnvType.CLIENT)
public final class CBCConnectedTextures {
	private CBCConnectedTextures() {
	}

	public static void register() {
		register(CBCBlocks.STEEL_CANNON_BARREL.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.STEEL_CANNON_BARREL));
		register(CBCBlocks.BUILT_UP_STEEL_CANNON_BARREL.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.BUILT_UP_STEEL_CANNON_BARREL));
		register(CBCBlocks.STEEL_CANNON_CHAMBER.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.STEEL_CANNON_CHAMBER));
		register(CBCBlocks.BUILT_UP_STEEL_CANNON_CHAMBER.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.BUILT_UP_STEEL_CANNON_CHAMBER));
		register(CBCBlocks.THICK_STEEL_CANNON_CHAMBER.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.THICK_STEEL_CANNON_CHAMBER));
		register(CBCBlocks.STEEL_QUICKFIRING_BREECH.get(), new SlidingBreechCTBehavior(CBCSpriteShifts.STEEL_SLIDING_BREECH_SIDE, CBCSpriteShifts.STEEL_SLIDING_BREECH_SIDE_HOLE));
		register(CBCBlocks.STEEL_SLIDING_BREECH.get(), new SlidingBreechCTBehavior(CBCSpriteShifts.STEEL_SLIDING_BREECH_SIDE, CBCSpriteShifts.STEEL_SLIDING_BREECH_SIDE_HOLE));
		register(CBCBlocks.NETHERSTEEL_CANNON_BARREL.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.NETHERSTEEL_CANNON_BARREL));
		register(CBCBlocks.BUILT_UP_NETHERSTEEL_CANNON_BARREL.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.BUILT_UP_NETHERSTEEL_CANNON_BARREL));
		register(CBCBlocks.NETHERSTEEL_CANNON_CHAMBER.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.NETHERSTEEL_CANNON_CHAMBER));
		register(CBCBlocks.BUILT_UP_NETHERSTEEL_CANNON_CHAMBER.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.BUILT_UP_NETHERSTEEL_CANNON_CHAMBER));
		register(CBCBlocks.THICK_NETHERSTEEL_CANNON_CHAMBER.get(), new BuiltUpCannonCTBehavior(CBCSpriteShifts.THICK_NETHERSTEEL_CANNON_CHAMBER));
	}

	private static void register(Block block, ConnectedTextureBehaviour behaviour) {
		AllModels.register(block, CTModel.of(behaviour));
	}
}
