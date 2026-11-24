package rbasamoyai.createbigcannons.compat.trinkets;

import eu.pb4.trinkets.api.TrinketsApi;
import rbasamoyai.createbigcannons.equipment.gas_mask.GasMaskItem;
import rbasamoyai.createbigcannons.CBCTags.CBCItemTags;
import rbasamoyai.createbigcannons.index.CBCItems;

// Adapted from Create's Trinkets class --ritchie
// 26.1 port: restored from upstream's 1.20.1 Fabric code, on Trinkets Updated (eu.pb4.trinkets). Gas masks go in
// the head/face slot (data/trinkets/...), which upstream widens to 2 so goggles and a gas mask fit together.
public class CBCTrinketsIntegration {

	public static void init() {
		GasMaskItem.addIsWearingPredicate(entity -> TrinketsApi.getAttachment(entity)
			.isEquipped(stack -> stack.is(CBCItemTags.GAS_MASKS), true));
		GasMaskItem.addOverlayDisplayPredicate(player -> TrinketsApi.getAttachment(player)
			.isEquipped(stack -> CBCItems.GAS_MASK.isIn(stack), true));
	}

}
