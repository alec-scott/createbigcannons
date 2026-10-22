package rbasamoyai.createbigcannons.base;

import rbasamoyai.createbigcannons.CreateBigCannons;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;

import net.minecraft.world.level.saveddata.SavedDataType;

import rbasamoyai.createbigcannons.utils.CBCValueIO;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import com.zurrtum.create.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class PartialBlockDamageSaveData extends SavedData {

	private static final int MAX_DAMAGES_PER_DIMENSION = 32768;

	private final Object2ObjectLinkedOpenHashMap<ResourceKey<Level>, Object2FloatLinkedOpenHashMap<BlockPos>> blockDamage = new Object2ObjectLinkedOpenHashMap<>();

	/**
	 * PORT-NOTE(mc26.1): saved data is codec-based ({@link SavedDataType}); the codec wraps the 1.21.1 compound layout.
	 * Following Create Fly, the file is now {@code data/createbigcannons/block_damage.dat}; damage stored in the old
	 * {@code createbigcannons_block_damage.dat} is not carried over.
	 */
	private static final SavedDataType<PartialBlockDamageSaveData> TYPE = new SavedDataType<>(CreateBigCannons.resource("block_damage"),
		PartialBlockDamageSaveData::new, CompoundTag.CODEC.xmap(PartialBlockDamageSaveData::load, PartialBlockDamageSaveData::save), null);

	private PartialBlockDamageSaveData() {
	}

	private CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		CompoundTag blockDamage = new CompoundTag();
		for (Map.Entry<ResourceKey<Level>, Object2FloatLinkedOpenHashMap<BlockPos>> entry : this.blockDamage.object2ObjectEntrySet()) {
			ListTag damages = new ListTag();
			for (Object2FloatMap.Entry<BlockPos> e : entry.getValue().object2FloatEntrySet()) {
				CompoundTag tag1 = new CompoundTag();
				tag1.put("Pos", CBCValueIO.writeBlockPos(e.getKey()));
				tag1.putFloat("Damage", e.getFloatValue());
				damages.add(tag1);
			}
			blockDamage.put(entry.getKey().identifier().toString(), damages);
		}
		tag.put("BlockDamage", blockDamage);
		return tag;
	}

	public Map<ResourceKey<Level>, Object2FloatLinkedOpenHashMap<BlockPos>> getBlockDamage() {
		return this.blockDamage;
	}

	private static PartialBlockDamageSaveData load(CompoundTag tag) {
		PartialBlockDamageSaveData savedata = new PartialBlockDamageSaveData();
		CompoundTag values = tag.getCompoundOrEmpty("BlockDamage");

		for (String key : values.keySet()) {
			ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, CBCUtils.location(key));
			ListTag damagesTag = values.getListOrEmpty(key);
			Object2FloatLinkedOpenHashMap<BlockPos> damages = new Object2FloatLinkedOpenHashMap<>(damagesTag.size());

			int len = Math.min(damagesTag.size(), MAX_DAMAGES_PER_DIMENSION);
			for (int i = 0; i < len; ++i) {
				CompoundTag entry = damagesTag.getCompoundOrEmpty(i);
				damages.put(CBCValueIO.readBlockPos(entry, "Pos").get(), entry.getFloatOr("Damage", 0));
			}
			savedata.blockDamage.put(dimensionKey, damages);
		}
		return savedata;
	}

	public static PartialBlockDamageSaveData load(MinecraftServer server) {
		return server.overworld()
			.getDataStorage()
			.computeIfAbsent(TYPE);
	}

}
