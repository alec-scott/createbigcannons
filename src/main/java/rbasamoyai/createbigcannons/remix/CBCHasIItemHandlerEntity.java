package rbasamoyai.createbigcannons.remix;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface CBCHasIItemHandlerEntity {
    @Nullable Storage<ItemVariant> getItemHandler();
}
