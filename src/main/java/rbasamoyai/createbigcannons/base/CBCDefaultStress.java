package rbasamoyai.createbigcannons.base;

import java.util.HashMap;
import java.util.Map;

import rbasamoyai.createbigcannons.port.registrate.builders.BlockBuilder;
import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * Avoiding concurrency issues in config from accessing BlockStressDefaults, also simplified to leave out generators
 * and capacity
 */
public class CBCDefaultStress {

	public static final Map<Identifier, Double> DEFAULT_IMPACTS = new HashMap<>();

	public static void setDefaultImpact(Identifier blockId, double impact) {
		DEFAULT_IMPACTS.put(blockId, impact);
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double impact) {
		return b -> {
			setDefaultImpact(CBCUtils.location(b.getOwner()
					.getModid(), b.getName()), impact);
			return b;
		};
	}

}
