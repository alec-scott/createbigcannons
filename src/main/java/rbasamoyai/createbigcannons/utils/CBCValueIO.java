package rbasamoyai.createbigcannons.utils;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Bridges CBC's NBT-shaped save code to 26.1's {@link ValueInput}/{@link ValueOutput}.
 *
 * <p>PORT-NOTE(mc26.1): block entity and entity data no longer hand out a {@link CompoundTag}. Nested compounds and lists
 * are stored through {@link ExtraCodecs#NBT}, so the saved data keeps exactly the shape it had on 1.21.1.</p>
 */
public final class CBCValueIO {

	private CBCValueIO() {
	}

	public static boolean contains(ValueInput input, String key) {
		return input.read(key, ExtraCodecs.NBT).isPresent();
	}

	public static CompoundTag getCompound(ValueInput input, String key) {
		return input.read(key, CompoundTag.CODEC).orElseGet(CompoundTag::new);
	}

	public static ListTag getList(ValueInput input, String key) {
		return input.read(key, ExtraCodecs.NBT).filter(ListTag.class::isInstance).map(ListTag.class::cast).orElseGet(ListTag::new);
	}

	@Nullable
	public static UUID getUUID(ValueInput input, String key) {
		return input.read(key, UUIDUtil.CODEC).orElse(null);
	}

	public static int[] getIntArray(ValueInput input, String key) {
		return input.getIntArray(key).orElseGet(() -> new int[0]);
	}

	public static void put(ValueOutput output, String key, Tag tag) {
		output.store(key, ExtraCodecs.NBT, tag);
	}

	/** Replaces 1.21.1 {@code CBCValueIO.saveItem(Provider, ItemStack)}: an empty stack saves as an empty compound. */
	public static Tag saveItem(HolderLookup.Provider registries, ItemStack stack) {
		return stack.isEmpty() ? new CompoundTag() : ItemStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
	}

	/** Replaces 1.21.1 {@code CBCValueIO.parseItem(Provider, CompoundTag)}. */
	public static ItemStack parseItem(HolderLookup.Provider registries, Tag tag) {
		if (tag instanceof CompoundTag compound && compound.isEmpty()) return ItemStack.EMPTY;
		return ItemStack.OPTIONAL_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
	}

	public static void putUUID(ValueOutput output, String key, UUID uuid) {
		output.store(key, UUIDUtil.CODEC, uuid);
	}

}
