package rbasamoyai.createbigcannons;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.fabricmc.loader.api.FabricLoader;
import rbasamoyai.createbigcannons.utils.CBCUtils;

// Copied from Create's Mods class --ritchie
public enum CBCMods {
	COPYCATS,
	CURIOS,
	FRAMEDBLOCKS,
    SABLE,
    SIMULATED;

	private final String id;
    private final boolean isLoaded;

	CBCMods() {
		this.id = name().toLowerCase(Locale.ROOT);
        this.isLoaded = FabricLoader.getInstance().isModLoaded(this.id);
	}

	public String id() {
		return this.id;
	}

	public Identifier resource(String path) {
		return CBCUtils.location(id, path);
	}

	public Block getBlock(String id) {
		return BuiltInRegistries.BLOCK.getValue(this.resource(id));
	}

	public boolean isLoaded() { return this.isLoaded; }

	public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
		return this.isLoaded() ? Optional.of(toRun.get().get()) : Optional.empty();
	}

	public void executeIfInstalled(Supplier<Runnable> toExecute) {
		if (isLoaded()) toExecute.get().run();
	}

}
