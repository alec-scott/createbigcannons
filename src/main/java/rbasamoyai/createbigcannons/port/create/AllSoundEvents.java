package rbasamoyai.createbigcannons.port.create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The sound-entry builder from upstream Create's {@code AllSoundEvents} (Create 6.0.10, MIT), without Create's own
 * sound list.
 *
 * <p>PORT-NOTE(fabric): Create Fly replaced this with a much simpler record that has no builder. CBC's sounds are defined
 * with the builder, so it lives here. NeoForge's {@code RegisterEvent}/{@code DeferredHolder} are replaced by direct
 * registration into {@link BuiltInRegistries#SOUND_EVENT} in {@link SoundEntry#register()}.</p>
 */
public class AllSoundEvents {

	public static final Map<Identifier, SoundEntry> ALL = new HashMap<>();

	public record ConfiguredSoundEvent(Supplier<SoundEvent> event, float volume, float pitch) {}

	public static class SoundEntryBuilder {

		protected Identifier id;
		protected String subtitle = "unregistered";
		protected SoundSource category = SoundSource.BLOCKS;
		protected List<ConfiguredSoundEvent> wrappedEvents;
		protected List<Identifier> variants;
		protected int attenuationDistance;

		public SoundEntryBuilder(Identifier id) {
			wrappedEvents = new ArrayList<>();
			variants = new ArrayList<>();
			this.id = id;
		}

		public SoundEntryBuilder subtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public SoundEntryBuilder attenuationDistance(int distance) {
			this.attenuationDistance = distance;
			return this;
		}

		public SoundEntryBuilder noSubtitle() {
			this.subtitle = null;
			return this;
		}

		public SoundEntryBuilder category(SoundSource category) {
			this.category = category;
			return this;
		}

		public SoundEntryBuilder addVariant(String name) {
			return addVariant(Identifier.fromNamespaceAndPath(id.getNamespace(), name));
		}

		public SoundEntryBuilder addVariant(Identifier id) {
			variants.add(id);
			return this;
		}

		public SoundEntryBuilder playExisting(Supplier<SoundEvent> event, float volume, float pitch) {
			wrappedEvents.add(new ConfiguredSoundEvent(event, volume, pitch));
			return this;
		}

		public SoundEntryBuilder playExisting(SoundEvent event, float volume, float pitch) {
			return playExisting(() -> event, volume, pitch);
		}

		public SoundEntryBuilder playExisting(SoundEvent event) {
			return playExisting(event, 1, 1);
		}

		public SoundEntryBuilder playExisting(Holder<SoundEvent> event) {
			return playExisting(event::value, 1, 1);
		}

		public SoundEntry build() {
			SoundEntry entry =
				wrappedEvents.isEmpty() ? new CustomSoundEntry(id, variants, subtitle, category, attenuationDistance)
					: new WrappedSoundEntry(id, subtitle, wrappedEvents, category, attenuationDistance);
			ALL.put(entry.getId(), entry);
			return entry;
		}

	}

	public static abstract class SoundEntry {

		protected Identifier id;
		protected String subtitle;
		protected SoundSource category;
		protected int attenuationDistance;

		public SoundEntry(Identifier id, String subtitle, SoundSource category, int attenuationDistance) {
			this.id = id;
			this.subtitle = subtitle;
			this.category = category;
			this.attenuationDistance = attenuationDistance;
		}

		public abstract void prepare();

		/** Registers this entry's sound event(s). */
		public abstract void register();

		public abstract void write(JsonObject json);

		public abstract Holder<SoundEvent> getMainEventHolder();

		public abstract SoundEvent getMainEvent();

		public String getSubtitleKey() {
			return id.getNamespace() + ".subtitle." + id.getPath();
		}

		public Identifier getId() {
			return id;
		}

		public boolean hasSubtitle() {
			return subtitle != null;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public void playOnServer(Level world, Vec3i pos) {
			playOnServer(world, pos, 1, 1);
		}

		public void playOnServer(Level world, Vec3i pos, float volume, float pitch) {
			play(world, null, pos, volume, pitch);
		}

		public void play(Level world, Player entity, Vec3i pos) {
			play(world, entity, pos, 1, 1);
		}

		public void playFrom(Entity entity) {
			playFrom(entity, 1, 1);
		}

		public void playFrom(Entity entity, float volume, float pitch) {
			if (!entity.isSilent())
				play(entity.level(), null, entity.blockPosition(), volume, pitch);
		}

		public void play(Level world, Player entity, Vec3i pos, float volume, float pitch) {
			play(world, entity, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, volume, pitch);
		}

		public void play(Level world, Player entity, Vec3 pos, float volume, float pitch) {
			play(world, entity, pos.x(), pos.y(), pos.z(), volume, pitch);
		}

		public abstract void play(Level world, Player entity, double x, double y, double z, float volume, float pitch);

		public void playAt(Level world, Vec3i pos, float volume, float pitch, boolean fade) {
			playAt(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, volume, pitch, fade);
		}

		public void playAt(Level world, Vec3 pos, float volume, float pitch, boolean fade) {
			playAt(world, pos.x(), pos.y(), pos.z(), volume, pitch, fade);
		}

		public abstract void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade);

	}

	private static class WrappedSoundEntry extends SoundEntry {

		private final List<ConfiguredSoundEvent> wrappedEvents;
		private final List<CompiledSoundEvent> compiledEvents = new ArrayList<>();

		public WrappedSoundEntry(Identifier id, String subtitle,
			List<ConfiguredSoundEvent> wrappedEvents, SoundSource category, int attenuationDistance) {
			super(id, subtitle, category, attenuationDistance);
			this.wrappedEvents = wrappedEvents;
		}

		@Override
		public void prepare() {
		}

		@Override
		public void register() {
			for (int i = 0; i < wrappedEvents.size(); i++) {
				ConfiguredSoundEvent wrapped = wrappedEvents.get(i);
				Identifier location = getIdOf(i);
				Holder<SoundEvent> event = Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, location,
					SoundEvent.createVariableRangeEvent(location));
				compiledEvents.add(new CompiledSoundEvent(event, wrapped.volume(), wrapped.pitch()));
			}
		}

		@Override
		public Holder<SoundEvent> getMainEventHolder() {
			return compiledEvents.getFirst().event();
		}

		@Override
		public SoundEvent getMainEvent() {
			return compiledEvents.getFirst().event().value();
		}

		protected Identifier getIdOf(int i) {
			return Identifier.fromNamespaceAndPath(id.getNamespace(), i == 0 ? id.getPath() : id.getPath() + "_compounded_" + i);
		}

		@Override
		public void write(JsonObject json) {
			for (int i = 0; i < wrappedEvents.size(); i++) {
				ConfiguredSoundEvent event = wrappedEvents.get(i);
				JsonObject entry = new JsonObject();
				JsonArray list = new JsonArray();
				JsonObject s = new JsonObject();
				s.addProperty("name", event.event().get().location().toString());
				s.addProperty("type", "event");
				if (attenuationDistance != 0)
					s.addProperty("attenuation_distance", attenuationDistance);
				list.add(s);
				entry.add("sounds", list);
				if (i == 0 && hasSubtitle())
					entry.addProperty("subtitle", getSubtitleKey());
				json.add(getIdOf(i).getPath(), entry);
			}
		}

		@Override
		public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
			for (CompiledSoundEvent event : compiledEvents) {
				world.playSound(entity, x, y, z, event.event().value(), category, event.volume() * volume,
					event.pitch() * pitch);
			}
		}

		@Override
		public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
			for (CompiledSoundEvent event : compiledEvents) {
				world.playLocalSound(x, y, z, event.event().value(), category, event.volume() * volume,
					event.pitch() * pitch, fade);
			}
		}

		private record CompiledSoundEvent(Holder<SoundEvent> event, float volume, float pitch) {
		}

	}

	private static class CustomSoundEntry extends SoundEntry {

		protected List<Identifier> variants;
		protected Holder<SoundEvent> event;

		public CustomSoundEntry(Identifier id, List<Identifier> variants, String subtitle,
			SoundSource category, int attenuationDistance) {
			super(id, subtitle, category, attenuationDistance);
			this.variants = variants;
		}

		@Override
		public void prepare() {
		}

		@Override
		public void register() {
			event = Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
		}

		@Override
		public Holder<SoundEvent> getMainEventHolder() {
			return event;
		}

		@Override
		public SoundEvent getMainEvent() {
			return event.value();
		}

		@Override
		public void write(JsonObject json) {
			JsonObject entry = new JsonObject();
			JsonArray list = new JsonArray();

			JsonObject s = new JsonObject();
			s.addProperty("name", id.toString());
			s.addProperty("type", "file");
			if (attenuationDistance != 0)
				s.addProperty("attenuation_distance", attenuationDistance);
			list.add(s);

			for (Identifier variant : variants) {
				s = new JsonObject();
				s.addProperty("name", variant.toString());
				s.addProperty("type", "file");
				if (attenuationDistance != 0)
					s.addProperty("attenuation_distance", attenuationDistance);
				list.add(s);
			}

			entry.add("sounds", list);
			if (hasSubtitle())
				entry.addProperty("subtitle", getSubtitleKey());
			json.add(id.getPath(), entry);
		}

		@Override
		public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
			world.playSound(entity, x, y, z, event.value(), category, volume, pitch);
		}

		@Override
		public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
			world.playLocalSound(x, y, z, event.value(), category, volume, pitch, fade);
		}

	}

}
