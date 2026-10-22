package rbasamoyai.createbigcannons.port.porting_lib.models.generators;

import rbasamoyai.createbigcannons.port.porting_lib.DatagenStubs;
import rbasamoyai.createbigcannons.port.porting_lib.data.ExistingFileHelper;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

/** Datagen stub; see {@link rbasamoyai.createbigcannons.port.porting_lib.DatagenStubs}. */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {
    public ItemModelProvider(final PackOutput output, final String modid, final ExistingFileHelper existingFileHelper) {
        super(output, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
    }

    public ItemModelBuilder basicItem(final Item item) {
        throw DatagenStubs.unsupported();
    }
}
