package rbasamoyai.createbigcannons.remix;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface CBCHasIItemHandlerBlockEntity {
    @Nullable Storage<ItemVariant> getItemHandler(Direction side);
}
