package rbasamoyai.createbigcannons.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Lets other mods stop CBC projectiles and explosions from damaging terrain at a position (e.g. claims).
 *
 * <p>PORT-NOTE(fabric): was a cancellable NeoForge bus event. Listeners return {@code false} to protect the position,
 * which matches cancelling the NeoForge event.</p>
 */
@FunctionalInterface
public interface ProjectileDamageEvent {

	Event<ProjectileDamageEvent> EVENT = EventFactory.createArrayBacked(ProjectileDamageEvent.class, listeners -> (level, pos) -> {
		for (ProjectileDamageEvent listener : listeners) {
			if (!listener.canDamageTerrain(level, pos))
				return false;
		}
		return true;
	});

	boolean canDamageTerrain(Level level, BlockPos pos);

}
