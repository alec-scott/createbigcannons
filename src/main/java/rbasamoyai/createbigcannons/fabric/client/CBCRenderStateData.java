package rbasamoyai.createbigcannons.fabric.client;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

/** Extra render-state data CBC attaches through Fabric's render-state API. */
public final class CBCRenderStateData {

	public static final RenderStateDataKey<Vehicle> CBC_VEHICLE = RenderStateDataKey.create(() -> "createbigcannons:vehicle");

	public enum Vehicle { NONE, CARRIAGE, CANNON }

	private CBCRenderStateData() {
	}

}
