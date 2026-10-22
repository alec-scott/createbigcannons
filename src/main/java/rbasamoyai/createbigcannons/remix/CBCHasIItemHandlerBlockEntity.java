package rbasamoyai.createbigcannons.remix;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandler;

public interface CBCHasIItemHandlerBlockEntity {
    @Nullable IItemHandler getItemHandler(Direction side);
}
