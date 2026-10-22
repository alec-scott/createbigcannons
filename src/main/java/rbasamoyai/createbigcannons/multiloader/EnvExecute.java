package rbasamoyai.createbigcannons.multiloader;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvExecute {
	public static void executeOnClient(Supplier<Runnable> sup) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			sup.get().run();
	}
}
