package rbasamoyai.createbigcannons.index;

import static rbasamoyai.createbigcannons.CreateBigCannons.REGISTRATE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import rbasamoyai.createbigcannons.CBCTags;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.ModGroup;
import rbasamoyai.createbigcannons.crafting.foundry.MoltenMetalLiquidBlock;
import rbasamoyai.createbigcannons.port.registrate.fabric.SimpleFlowableFluid;
import rbasamoyai.createbigcannons.port.registrate.util.entry.FluidEntry;
import rbasamoyai.createbigcannons.utils.CBCRegistryUtils;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * PORT-NOTE(fabric): NeoForge's {@code BaseFlowingFluid}/{@code FluidType} are replaced by the port Registrate's Fabric
 * fluid builder, as upstream's 1.20.1 Fabric build did. The NeoForge fluid type also carried viscosity, density,
 * temperature and the submerged fog colour/distance; Fabric has no equivalent of the first three and the fog is not
 * ported (see PORTING-26.1.md). Rendering uses the still/flowing textures through the builder's default renderer.
 */
public class CBCFluids {

    static {
        ModGroup.setDefaultTabToNull();
    }

	public static final FluidEntry<SimpleFlowableFluid.Flowing>
		MOLTEN_CAST_IRON = moltenMetal("molten_cast_iron", "Molten Cast Iron"),
		MOLTEN_BRONZE = moltenMetal("molten_bronze", "Molten Bronze"),
		MOLTEN_STEEL = moltenMetal("molten_steel", "Molten Steel"),
		MOLTEN_NETHERSTEEL = moltenMetal("molten_nethersteel", "Molten Nethersteel");

	private static FluidEntry<SimpleFlowableFluid.Flowing> moltenMetal(String name, String lang) {
		return REGISTRATE.fluid(name, CreateBigCannons.resource("fluid/" + name + "_still"), CreateBigCannons.resource("fluid/" + name + "_flow"))
			.lang(lang)
			.tag(commonTag(name), CBCTags.CBCFluidTags.MOLTEN_METAL)
			.fluidProperties(p -> p.levelDecreasePerBlock(2)
				.tickRate(25)
				.flowSpeed(3)
				.blastResistance(100f))
			.source(SimpleFlowableFluid.Source::new)
			.block(MoltenMetalLiquidBlock::new).build()
			.bucket()
			.onRegister(CBCFluids::registerFluidDispenseBehavior)
			.build()
			.register();
	}

    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
    private static final DispenseItemBehavior DISPENSE_FLUID = new DefaultDispenseItemBehavior() {
        @Override
        protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
            DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) pStack.getItem();
            BlockPos pos = pSource.pos().relative(pSource.state().getValue(DispenserBlock.FACING));
            Level level = pSource.level();
            if (dispensibleContainerItem.emptyContents(null, level, pos, null))
                return new ItemStack(Items.BUCKET);
            return DEFAULT.dispense(pSource, pStack);
        }
    };

    private static void registerFluidDispenseBehavior(BucketItem bucket) {
        DispenserBlock.registerBehavior(bucket, DISPENSE_FLUID);
    }

	public static void register() {}

	private static TagKey<Fluid> commonTag(String path) {
		return TagKey.create(CBCRegistryUtils.getFluidRegistryKey(), CBCUtils.location("c", path));
	}

}
