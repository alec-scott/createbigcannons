package rbasamoyai.createbigcannons.munitions.big_cannon;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import java.util.List;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.api.schematic.nbt.PartialSafeNBT;
import com.zurrtum.create.api.schematic.requirement.SpecialBlockEntityItemRequirement;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import com.zurrtum.create.foundation.blockEntity.SyncedBlockEntity;
import com.zurrtum.create.client.foundation.utility.CreateLang;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCItems;

public class BigCannonProjectileBlockEntity extends SyncedBlockEntity implements IHaveGoggleInformation, Container, PartialSafeNBT,
    SpecialBlockEntityItemRequirement {

    protected ItemStack tracer = ItemStack.EMPTY;

	public BigCannonProjectileBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.put("Tracer", this.getTracer().saveOptional(registries));
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        this.setTracer(ItemStack.parseOptional(registries, tag.getCompound("Tracer")));
    }

    @Override
    public void writeSafe(ValueOutput tag) {
        tag.put("Tracer", this.getTracer().saveOptional(registries));
    }

    protected void writeSafeComponents(PatchedDataComponentMap safeComponents) {
        ItemStack tracer = this.getTracer();
        if (tracer.isEmpty()) {
            safeComponents.set(CBCDataComponents.TRACER, ItemContainerContents.EMPTY);
        } else {
            safeComponents.set(CBCDataComponents.TRACER, ItemContainerContents.fromItems(List.of(tracer)));
        }
    }

    protected void writeSafeComponentsForItemRequirement(PatchedDataComponentMap safeComponents) {
        this.writeSafeComponents(safeComponents);
    }

    @Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.getTracer().isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot == 0 ? this.getTracer() : ItemStack.EMPTY;
	}

	public ItemStack getTracer() { return this.tracer.copy(); }

    public void setTracer(ItemStack itemStack) { this.tracer = itemStack == null ? ItemStack.EMPTY : itemStack.copy(); }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        this.tracer = componentInput.getOrDefault(CBCDataComponents.TRACER, ItemContainerContents.EMPTY).copyOne();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(CBCDataComponents.TRACER, ItemContainerContents.fromItems(List.of(this.tracer)));
    }

    @Override
	public ItemStack removeItem(int slot, int amount) {
		if (this.isEmpty() || slot != 0 || amount < 1)
			return ItemStack.EMPTY;
        ItemStack originalCopy = this.getTracer();
        ItemStack result = originalCopy.split(amount);
        this.setTracer(originalCopy);
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (this.isEmpty() || slot != 0)
			return ItemStack.EMPTY;
		ItemStack result = this.getTracer();
		this.setTracer(ItemStack.EMPTY);
		return result;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot != 0)
			return;
		this.setTracer(stack);
		if (stack.getCount() > this.getMaxStackSize())
			stack.setCount(this.getMaxStackSize());
		this.setChanged();
	}

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return index == 0 && CBCItems.TRACER_TIP.isIn(stack) && this.getTracer().isEmpty();
    }

    @Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public void clearContent() {
		this.setTracer(ItemStack.EMPTY);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (!this.getTracer().isEmpty())
			CreateLang.builder("tooltip")
			.translate("createbigcannons.tracer")
			.forGoggles(tooltip);
		return true;
	}

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        ItemStack stack = new ItemStack(this.getBlockState().getBlock().asItem());
        if (stack.isEmpty())
            return ItemRequirement.INVALID;
        PatchedDataComponentMap safeComponents = new PatchedDataComponentMap(DataComponentMap.EMPTY);
        this.writeSafeComponentsForItemRequirement(safeComponents);
        stack.applyComponents(safeComponents);
        return new ItemRequirement(new ItemRequirement.StrictNbtStackRequirement(stack, ItemRequirement.ItemUseType.CONSUME));
    }

}
