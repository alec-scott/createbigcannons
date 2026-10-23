package rbasamoyai.createbigcannons.datagen.assets;

import net.minecraft.world.level.storage.loot.LootContext;

import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllBlockTags;
import com.zurrtum.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.zurrtum.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import rbasamoyai.createbigcannons.port.create.foundation.data.BlockStateGen;
import rbasamoyai.createbigcannons.port.registrate.builders.BlockBuilder;
import rbasamoyai.createbigcannons.port.registrate.builders.ItemBuilder;
import rbasamoyai.createbigcannons.port.registrate.providers.DataGenContext;
import rbasamoyai.createbigcannons.port.registrate.providers.RegistrateBlockstateProvider;
import rbasamoyai.createbigcannons.port.registrate.providers.loot.RegistrateBlockLootTables;
import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullBiConsumer;
import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullFunction;
import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import rbasamoyai.createbigcannons.port.porting_lib.models.generators.BlockModelBuilder;
import rbasamoyai.createbigcannons.port.porting_lib.models.generators.ConfiguredModel;
import rbasamoyai.createbigcannons.CBCTags;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlock;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageBlock;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageBlockItem;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlock;
import rbasamoyai.createbigcannons.cannon_loading.CannonLoaderGen;
import rbasamoyai.createbigcannons.cannons.autocannon.AutocannonBarrelBlock;
import rbasamoyai.createbigcannons.cannons.autocannon.AutocannonBlock;
import rbasamoyai.createbigcannons.cannons.autocannon.AutocannonBlockItem;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlock;
import rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlock;
import rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlockItem;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechBlockGen;
import rbasamoyai.createbigcannons.crafting.boring.CannonDrillGen;
import rbasamoyai.createbigcannons.crafting.builtup.CannonBuilderGen;
import rbasamoyai.createbigcannons.crafting.builtup.CannonBuilderHeadBlock;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastMouldBlock;
import rbasamoyai.createbigcannons.crafting.incomplete.IncompleteScrewBreechBlockGen;
import rbasamoyai.createbigcannons.crafting.incomplete.IncompleteSlidingBreechBlockGen;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerBlock;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerItem;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonMunitionBlock;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.BigCannonPropellantBlock;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.BigCartridgeBlock;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.BigCartridgeBlockItem;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class CBCBuilderTransformers {

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonBarrel(String material, boolean bored) {
		NonNullUnaryOperator<BlockBuilder<T, P>> b1 = cannonPart(CreateBigCannons.resource("block/cannon_barrel"),
			"cannon_barrel/" + material + "_cannon_barrel_side",
			"cannon_barrel/" + (bored ? "" : "unbored_") + material + "_cannon_barrel_end");
		return bored ? b1.andThen(b -> b.tag(CBCTags.CBCBlockTags.REDUCES_SPREAD)) : b1;
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> builtUpCannonBarrel(String material, boolean bored) {
		NonNullUnaryOperator<BlockBuilder<T, P>> b1 = cannonPart(CreateBigCannons.resource("block/built_up_cannon_barrel"),
			"cannon_barrel/built_up_" + material + "_cannon_barrel_side",
			"cannon_barrel/" + (bored ? "" : "unbored_") + "built_up_" + material + "_cannon_barrel_end");
		return bored ? b1.andThen(b -> b.tag(CBCTags.CBCBlockTags.REDUCES_SPREAD)) : b1;
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonChamber(String material, boolean bored) {
		NonNullUnaryOperator<BlockBuilder<T, P>> b1 = cannonPart(CBCUtils.location("block/cube_column"),
			"cannon_chamber/" + material + "_cannon_chamber_side",
			"cannon_chamber/" + (bored ? "" : "unbored_") + material + "_cannon_chamber_end");
		return bored ? b1.andThen(b -> b.tag(CBCTags.CBCBlockTags.THICK_TUBING).tag(CBCTags.CBCBlockTags.REDUCES_SPREAD)) : b1;
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> builtUpCannonChamber(String material, boolean bored) {
		NonNullUnaryOperator<BlockBuilder<T, P>> b1 = cannonPart(CreateBigCannons.resource("block/built_up_cannon_chamber"),
			"cannon_chamber/built_up_" + material + "_cannon_chamber_side",
			"cannon_chamber/" + (bored ? "" : "unbored_") + "built_up_" + material + "_cannon_chamber_end");
		return bored ? b1.andThen(b -> b.tag(CBCTags.CBCBlockTags.THICK_TUBING).tag(CBCTags.CBCBlockTags.REDUCES_SPREAD)) : b1;
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> thickCannonChamber(String material, boolean bored) {
		NonNullUnaryOperator<BlockBuilder<T, P>> b1 = cannonPart(CreateBigCannons.resource("block/thick_cannon_chamber"),
			"cannon_chamber/thick_" + material + "_cannon_chamber_side",
			"cannon_chamber/" + (bored ? "" : "unbored_") + "thick_" + material + "_cannon_chamber_end");
		return bored ? b1.andThen(b -> b.tag(CBCTags.CBCBlockTags.THICK_TUBING).tag(CBCTags.CBCBlockTags.REDUCES_SPREAD)) : b1;
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonPart(Identifier model, String side, String end) {
		Identifier sideLoc = CreateBigCannons.resource("block/" + side);
		Identifier endLoc = CreateBigCannons.resource("block/" + end);
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().withExistingParent(c.getName(), model)
				.texture("side", sideLoc)
				.texture("end", endLoc)
				.texture("particle", sideLoc)));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> sizedHollowCannon(String sizePath, String pathAndMaterial) {
		Identifier baseLoc = CreateBigCannons.resource("block/" + sizePath + "_cannon_tube");
		Identifier tubeLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_tube");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().withExistingParent(c.getName(), baseLoc)
				.texture("tube", tubeLoc)));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonEnd(String pathAndMaterial) {
		Identifier baseLoc = CreateBigCannons.resource("block/cannon_end");
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_side");
		Identifier topLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_top");
		Identifier bottomLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_bottom");
		Identifier knobLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_knob");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().withExistingParent(c.getName(), baseLoc)
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc)
				.texture("knob", knobLoc)
				.texture("particle", topLoc)));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> dropMortarEnd(String pathAndMaterial) {
		Identifier baseLoc = CreateBigCannons.resource("block/drop_mortar_end");
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_side");
		Identifier topLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_top");
		Identifier bottomLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_bottom");
		Identifier knobLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_cannon_end_knob");
		Identifier spikeLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_drop_mortar_end_spike");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().withExistingParent(c.getName(), baseLoc)
				.texture("spike", spikeLoc)
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc)
				.texture("knob", knobLoc)
				.texture("particle", topLoc)));
	}

	public static <T extends Block & BigCannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> slidingBreech(String pathAndMaterial) {
		Identifier itemBaseLoc = CreateBigCannons.resource("block/sliding_breech_item");
		Identifier holeLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_hole");
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_side");
		Identifier sideHoleLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_side_hole");
		Identifier insideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_inside");
		Identifier breechblockTopLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_top");
		Identifier breechblockEndLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_end");
		Identifier breechblockSideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_side");
		Identifier breechblockBottomLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_bottom");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(SlidingBreechBlockGen.create(pathAndMaterial)::generate)
			.item(BigCannonBlockItem::new)
			.model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(itemBaseLoc))
				.texture("hole", holeLoc)
				.texture("side", sideLoc)
				.texture("side_hole", sideHoleLoc)
				.texture("inside", insideLoc)
				.texture("breechblock_top", breechblockTopLoc)
				.texture("breechblock_end", breechblockEndLoc)
				.texture("breechblock_side", breechblockSideLoc)
				.texture("breechblock_bottom", breechblockBottomLoc))
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> slidingBreechUnbored(String pathAndMaterial) {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(SlidingBreechBlockGen.create(pathAndMaterial)::generate);
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> slidingBreechIncomplete(String pathAndMaterial) {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(IncompleteSlidingBreechBlockGen.create(pathAndMaterial)::generate);
	}

	public static <T extends Block & BigCannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> screwBreech(String pathAndMaterial) {
		Identifier baseLoc = CreateBigCannons.resource("block/screw_breech");
		Identifier itemBaseLoc = CreateBigCannons.resource("block/screw_breech_item");
		Identifier topLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_screw_breech_top");
		Identifier bottomLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_screw_breech_bottom");
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_screw_breech_side");
		Identifier lockLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_screw_lock");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().withExistingParent(c.getName(), baseLoc)
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc)
				.texture("particle", topLoc)))
			.item(BigCannonBlockItem::new)
			.model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(itemBaseLoc))
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc)
				.texture("lock", lockLoc))
			.build();
	}

	public static <T extends Block & BigCannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> screwBreechUnbored(String pathAndMaterial, String typePathAndMaterial) {
		Identifier baseLoc = CreateBigCannons.resource("block/screw_breech");
		Identifier itemBaseLoc = CreateBigCannons.resource("block/screw_breech_item");
		Identifier topLoc = CreateBigCannons.resource("block/" + typePathAndMaterial + "_screw_breech_top");
		Identifier bottomLoc = CreateBigCannons.resource("block/" + typePathAndMaterial + "_screw_breech_bottom");
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_screw_breech_side");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().withExistingParent(c.getName(), baseLoc)
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc)
				.texture("particle", topLoc)))
			.item(BigCannonBlockItem::new)
			.model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(itemBaseLoc))
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc))
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> screwBreechIncomplete(String pathAndMaterial) {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(IncompleteScrewBreechBlockGen.create(pathAndMaterial)::generate);
	}

	public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> screwLock(String pathAndMaterial) {
		Identifier baseLoc = CreateBigCannons.resource("item/screw_lock");
		Identifier lockLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_screw_lock");
		return b -> b.model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(baseLoc))
			.texture("lock", lockLoc));
	}

	public static <T extends Block & AutocannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> autocannonBarrel(String pathAndMaterial) {
		Identifier texLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s -> {
				String name = c.getName() + "_" + (s.getValue(AutocannonBarrelBlock.ASSEMBLED) ? "assembled" : s.getValue(AutocannonBarrelBlock.BARREL_END).getSerializedName());
				if (s.getValue(AutocannonBarrelBlock.ASSEMBLED))
					return p.models().getBuilder(name).texture("particle", texLoc);
				Identifier loc = switch (s.getValue(AutocannonBarrelBlock.BARREL_END)) {
					case FLANGED -> CreateBigCannons.resource("block/autocannon/barrel_flanged");
					default -> CreateBigCannons.resource("block/autocannon/barrel");
				};
				return p.models().withExistingParent(name, loc).texture("material", texLoc);
			}))
			.item(AutocannonBlockItem::new)
			.model((c, p) -> p.withExistingParent(c.getName(), CreateBigCannons.resource("block/autocannon/barrel")).texture("material", texLoc))
			.build();
	}

	public static <T extends Block & AutocannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> autocannonBreech(String pathAndMaterial, boolean complete) {
		Identifier texLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon");
		Identifier tex1Loc = CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon_1");
		Identifier baseLoc = CreateBigCannons.resource("block/autocannon/breech");
		Identifier handleLoc = CreateBigCannons.resource("block/autocannon/breech_handle");
		NonNullUnaryOperator<BlockBuilder<T, P>> result = b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p,
				s -> {
					boolean handle = s.hasProperty(AutocannonBreechBlock.HANDLE) && s.getValue(AutocannonBreechBlock.HANDLE);
					return p.models().withExistingParent(handle ? c.getName() + "_handle" : c.getName(), handle ? handleLoc : baseLoc)
						.texture("material", texLoc)
						.texture("handle", tex1Loc);
				}));
		if (complete) {
			result = result.andThen(b -> b.item(AutocannonBlockItem::new)
				.model((c, p) -> p.withExistingParent(c.getName(), CreateBigCannons.resource("block/autocannon/breech_item")).texture("material", texLoc))
				.build());
		} else {
			result = result.andThen(b -> b.item(AutocannonBlockItem::new)
				.model((c, p) -> p.blockItem(c))
				.build());
		}
		return result;
	}

	public static <T extends Block & AutocannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> autocannonRecoilSpring(String pathAndMaterial, boolean complete) {
		Identifier texLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon");
		NonNullUnaryOperator<BlockBuilder<T, P>> result = b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p,
				$ -> p.models().withExistingParent(c.getName(), CreateBigCannons.resource("block/autocannon/recoil_spring")).texture("material", texLoc)));
		if (complete) {
			result = result.andThen(b -> b.item(AutocannonBlockItem::new)
				.model((c, p) -> p.withExistingParent(c.getName(), CreateBigCannons.resource("block/autocannon/recoil_spring_item")).texture("material", texLoc))
				.build());
		} else {
			result = result.andThen(b -> b.item(AutocannonBlockItem::new)
				.model((c, p) -> p.blockItem(c))
				.build());
		}
		return result;
	}

	public static <T extends Block & AutocannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredAutocannonBarrel(String pathAndMaterial) {
		return unboredAutocannonBlock(CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon"),
			CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon_1"),
			CreateBigCannons.resource("block/autocannon/unbored_barrel"));
	}

	public static <T extends Block & AutocannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredAutocannonRecoilSpring(String pathAndMaterial) {
		return unboredAutocannonBlock(CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon"),
			CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon_1"),
			CreateBigCannons.resource("block/autocannon/unbored_recoil_spring"));
	}

	public static <T extends Block & AutocannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredAutocannonBreech(String pathAndMaterial) {
		return unboredAutocannonBlock(CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon"),
			CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon_1"),
			CreateBigCannons.resource("block/autocannon/unbored_breech"));
	}

	private static <T extends Block & AutocannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredAutocannonBlock(Identifier tex, Identifier tex1, Identifier model) {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p,
				$ -> p.models().withExistingParent(c.getName(), model)
					.texture("material", tex)
					.texture("material1", tex1)))
			.item(AutocannonBlockItem::new)
			.model((c, p) -> p.blockItem(c))
			.build();
	}

	public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> autocannonBreechExtractor(String pathAndMaterial) {
		return b -> b.model((c, p) -> p.getBuilder(c.getName())
			.parent(p.getExistingFile(CreateBigCannons.resource("block/autocannon/extractor")))
			.texture("material", CreateBigCannons.resource("block/" + pathAndMaterial + "_autocannon")));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> ramHead() {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(BlockStateGen.directionalBlockProvider(false));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> wormHead() {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(BlockStateGen.directionalBlockProvider(false));
	}

	public static <T extends DirectionalAxisKineticBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonLoader() {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(CannonLoaderGen.create()::generate)
			.item()
			.model((c, p) -> {})
			.build();
	}

	public static <T extends DirectionalAxisKineticBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonDrill() {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(CannonDrillGen.create()::generate)
			.item()
			.model((c, p) -> {})
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonDrillBit() {
		return b -> b.properties(p -> p.noOcclusion())
			.loot((t, p) -> t.dropOther(p, AllBlocks.PISTON_EXTENSION_POLE))
			.blockstate(BlockStateGen.directionalBlockProvider(false));
	}

	public static <T extends DirectionalAxisKineticBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonBuilder() {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate(CannonBuilderGen.create()::generate)
			.item()
			.model((c, p) -> {})
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonBuilderHead() {
		Identifier notAttachedLoc = CreateBigCannons.resource("block/cannon_builder/cannon_builder_head");
		Identifier attachedLoc = CreateBigCannons.resource("block/cannon_builder/cannon_builder_head_attached");
		return b -> b.properties(p -> p.noOcclusion())
			.loot((t, p) -> t.dropOther(p, AllBlocks.PISTON_EXTENSION_POLE))
			.blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s ->
				p.models().getExistingFile(s.getValue(CannonBuilderHeadBlock.ATTACHED) ? attachedLoc : notAttachedLoc)));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> projectileLegacy(String pathAndMaterial) {
		return projectile(pathAndMaterial, true);
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> projectile(String pathAndMaterial) {
		return projectile(pathAndMaterial, false);
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> projectile(String pathAndMaterial, boolean useStandardModel) {
		Identifier baseLoc = CreateBigCannons.resource(String.format("block/%sprojectile_block", useStandardModel ? "standard_" : ""));
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial);
		Identifier topLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_top");
		Identifier bottomLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_bottom");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> {
				BlockModelBuilder builder = p.models().withExistingParent(c.getName(), baseLoc)
					.texture("side", sideLoc)
					.texture("top", topLoc)
					.texture("particle", topLoc);
				if (!useStandardModel) builder.texture("bottom", bottomLoc);
				p.directionalBlock(c.get(), builder);
			});
	}

	public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> fuzedProjectileItem(String pathAndMaterial) {
		Identifier headFuzeLoc = CreateBigCannons.resource("block/projectile_block_fuze_head");
		Identifier baseFuzeLoc = CreateBigCannons.resource("block/projectile_block_fuze_base");
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial);
		Identifier topLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_top");
		Identifier bottomLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_bottom");
		return b -> b.model((c, p) -> {
			var headFuzeModel = p.withExistingParent(c.getName() + "_head_fuze", headFuzeLoc)
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc)
				.texture("particle", topLoc);
			var baseFuzeModel = p.withExistingParent(c.getName() + "_base_fuze", baseFuzeLoc)
				.texture("side", sideLoc)
				.texture("top", topLoc)
				.texture("bottom", bottomLoc)
				.texture("particle", topLoc);
			p.blockItem(c);
		});
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> powderCharge() {
		Identifier baseLoc = CreateBigCannons.resource("block/powder_charge");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> BlockStateGen.axisBlock(c, p, $ -> p.models().getExistingFile(baseLoc)))
			.loot((t, c) -> {
				t.add(c, LootTable.lootTable()
					.withPool(t.applyExplosionCondition(c, LootPool.lootPool())
						.add(LootItem.lootTableItem(c))
						.apply(SetComponentsFunction.setComponent(CBCDataComponents.DAMP, true))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(c)
							.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BigCannonMunitionBlock.DAMP, true))))
					.withPool(t.applyExplosionCondition(c, LootPool.lootPool())
						.add(LootItem.lootTableItem(c))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(c)
							.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BigCannonMunitionBlock.DAMP, false)))));
			});
	}

	public static <T extends BigCartridgeBlock & BigCannonPropellantBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> bigCartridge() {
		Identifier filledLoc = CreateBigCannons.resource("block/big_cartridge_filled");
		Identifier emptyLoc = CreateBigCannons.resource("block/big_cartridge_empty");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s -> {
				return p.models().getExistingFile(s.getValue(BigCartridgeBlock.FILLED) ? filledLoc : emptyLoc);
			}))
			.loot((t, c) -> {
				CompoundTag dampTag = new CompoundTag();
				dampTag.putBoolean("Damp", true);
				t.add(c, LootTable.lootTable()
					.withPool(t.applyExplosionCondition(c, LootPool.lootPool()
							.add(LootItem.lootTableItem(c))
							.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
							.apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY).include(CBCDataComponents.POWER)))
						.apply(SetComponentsFunction.setComponent(CBCDataComponents.DAMP, true))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(c)
							.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BigCannonMunitionBlock.DAMP, true))))
					.withPool(t.applyExplosionCondition(c, LootPool.lootPool()
							.add(LootItem.lootTableItem(c))
							.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY).include(CBCDataComponents.POWER)))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(c)
							.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BigCannonMunitionBlock.DAMP, false)))));
			})
			.item(BigCartridgeBlockItem::new)
			.tag(CBCTags.CBCItemTags.BIG_CANNON_CARTRIDGES)
			.model((c, p) -> {
				p.withExistingParent(c.getName(), emptyLoc);
			})
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> autocannonAmmoContainer(boolean isCreative) {
		String root = isCreative ? "creative": "regular";
		return b -> b
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
				.forAllStatesExcept(state -> {
					AutocannonAmmoContainerBlock.State containerState = state.getValue(AutocannonAmmoContainerBlock.CONTAINER_STATE);
					String suffix = switch (containerState) {
						case CLOSED -> "";
						case EMPTY -> "_empty";
						case FILLED -> "_filled";
					};
					Identifier loc = p.modLoc("block/autocannon_ammo_containers/" + root + suffix);
					Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
					return ConfiguredModel.builder()
						.modelFile(p.models().getExistingFile(loc))
						.rotationY(axis == Direction.Axis.X ? 90 : 0)
						.build();
				}, BlockStateProperties.WATERLOGGED))
			.loot((t, c) -> {
				CopyComponentsFunction.Builder func = CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
					.include(CBCDataComponents.AMMO)
					.include(CBCDataComponents.TRACERS)
					.include(CBCDataComponents.TRACER_SPACING);
				if (isCreative)
					func = func.include(CBCDataComponents.CURRENT_INDEX);
				t.add(c, LootTable.lootTable()
					.withPool(t.applyExplosionCondition(c, LootPool.lootPool()
							.add(LootItem.lootTableItem(c))
							.apply(CopyNameFunction.copyName(LootContext.BlockEntityTarget.BLOCK_ENTITY)))
						.apply(func)));
			})
			.item(AutocannonAmmoContainerItem::new)
			.properties(p -> p.stacksTo(1))
			.properties(p -> isCreative ? p.rarity(Rarity.EPIC) : p)
			.tag(CBCTags.CBCItemTags.AUTOCANNON_AMMO_CONTAINERS)
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/autocannon_ammo_containers/" + root)))
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonMount() {
		Identifier baseLoc = CreateBigCannons.resource("block/cannon_mount/cannon_mount");
		Identifier flipLoc = CreateBigCannons.resource("block/cannon_mount/cannon_mount_flipped");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
				Identifier loc = state.getValue(CannonMountBlock.VERTICAL_DIRECTION) == Direction.DOWN ? baseLoc : flipLoc;
				return p.models().getExistingFile(loc);
			}, 0))
			.item()
			.model((c, p) -> {})
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> yawController() {
		Identifier baseLoc = CreateBigCannons.resource("block/cannon_mount/yaw_controller");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.simpleBlock(c.get(), p.models().getExistingFile(baseLoc)))
			.item()
			.model((c, p) -> {})
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonMountExtension() {
		Identifier baseLoc = CreateBigCannons.resource("block/cannon_mount/cannon_mount_extension");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().getExistingFile(baseLoc)))
			.item()
			.model((c, p) -> {})
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> fixedCannonMount() {
		String key = "block/cannon_mount/fixed_cannon_mount_";
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), state -> {
				Direction rotation = state.getValue(FixedCannonMountBlock.ROTATION);
				Identifier loc = CreateBigCannons.resource(key + rotation.getName());
				return p.models().getExistingFile(loc);
			}))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), CreateBigCannons.resource(key + "north")))
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonCarriage() {
		Identifier blockLoc = CreateBigCannons.resource("block/cannon_carriage/block");
		Identifier saddleLoc = CreateBigCannons.resource("block/cannon_carriage/block_saddle");
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.horizontalBlock(c.get(), s -> p.models().getExistingFile(s.getValue(CannonCarriageBlock.SADDLED) ? saddleLoc : blockLoc)))
			.loot((t, u) -> t.add(u, LootTable.lootTable()
				.withPool(LootPool.lootPool()
					.add(t.applyExplosionDecay(u, LootItem.lootTableItem(u))))
				.withPool(LootPool.lootPool()
					.add(t.applyExplosionDecay(u, LootItem.lootTableItem(Items.SADDLE)))
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(u)
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CannonCarriageBlock.SADDLED, true))))))
			.item(CannonCarriageBlockItem::new)
			.model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(blockLoc)))
			.build();
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> invisibleWithParticle(String path) {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.simpleBlock(c.get(), p.models().getBuilder(c.getName())
				.texture("particle", CreateBigCannons.resource(path))));
	}

	public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> slidingBreechblock(String pathAndMaterial) {
		Identifier baseLoc = CreateBigCannons.resource("item/sliding_breechblock");
		Identifier topLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_top");
		Identifier endLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_end");
		Identifier sideLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_side");
		Identifier bottomLoc = CreateBigCannons.resource("block/" + pathAndMaterial + "_sliding_breech_breechblock_bottom");
		return b -> b.model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(baseLoc))
			.texture("top", topLoc)
			.texture("end", endLoc)
			.texture("side", sideLoc)
			.texture("bottom", bottomLoc));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> castMould(String size) {
		Identifier baseLoc = CreateBigCannons.resource("block/cast_mould/" + size + "_cast_mould");
		Identifier sandLoc = CreateBigCannons.resource("block/casting_sand");
		return b -> b.properties(p -> p.mapColor(MapColor.PODZOL))
			.properties(p -> p.strength(2.0f, 3.0f))
			.properties(p -> p.sound(SoundType.WOOD))
			.properties(p -> p.noOcclusion())
			.tag(BlockTags.MINEABLE_WITH_AXE)
			.blockstate((c, p) -> p.getMultipartBuilder(c.get())
				.part()
				.modelFile(p.models().getExistingFile(baseLoc))
				.addModel()
				.end()
				.part()
				.modelFile(p.models().getExistingFile(sandLoc))
				.addModel()
				.condition(CannonCastMouldBlock.SAND, true)
				.end())
            .onRegister(c -> SchematicRequirementRegistries.BLOCKS.register(c, (state, be) -> {
                List<ItemStack> stacks = new ArrayList<>();
                stacks.add(new ItemStack(state.getBlock()));
                if (state.getValue(CannonCastMouldBlock.SAND))
                    stacks.add(CBCBlocks.CASTING_SAND.asStack());
                return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stacks);
            }))
			.item()
			.model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(baseLoc)))
			.build();
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> castIronScrapLoot(int count) {
		return (t, u) -> t.add(u, t.createSingleItemTableWithSilkTouch(u, CBCItems.CAST_IRON_NUGGET.get(), ConstantValue.exactly(count)));
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> bronzeScrapLoot(int count) {
		return (t, u) -> t.add(u, t.createSingleItemTableWithSilkTouch(u, CBCItems.BRONZE_SCRAP.get(), ConstantValue.exactly(count)));
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> steelScrapLoot(int count) {
		return (t, u) -> t.add(u, t.createSingleItemTableWithSilkTouch(u, CBCItems.STEEL_SCRAP.get(), ConstantValue.exactly(count)));
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> nethersteelScrapLoot(int count) {
		return (t, u) -> t.add(u, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(CBCItems.NETHERSTEEL_NUGGET.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(count))))));
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> tracerProjectileLoot() {
		return tracerProjectileLoot(t -> t);
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> tracerProjectileLoot(
            NonNullFunction<CopyComponentsFunction.Builder, CopyComponentsFunction.Builder> additionalCopyData) {
		return (t, u) -> t.add(u, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0f))
				.add(LootItem.lootTableItem(u)
					.apply(CopyNameFunction.copyName(LootContext.BlockEntityTarget.BLOCK_ENTITY))
					.apply(additionalCopyData.apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
						.include(CBCDataComponents.TRACER))))));
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> shellLoot(
            NonNullFunction<CopyComponentsFunction.Builder, CopyComponentsFunction.Builder> additionalCopyData) {
		return tracerProjectileLoot(f -> additionalCopyData.apply(f).include(CBCDataComponents.FUZE));
	}

	public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> shellLoot() {
		return shellLoot(t -> t);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> builtUpCannon() {
		return (c, p) -> {
			Identifier texLoc = CreateBigCannons.resource("block/cannon_chamber/steel_cannon_chamber_side");
			p.directionalBlock(c.get(), p.models().getBuilder(c.getName()).texture("particle", texLoc));
		};
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleBlock(String path) {
		return (c, p) -> p.simpleBlock(c.get(), p.models().getExistingFile(CreateBigCannons.resource(path)));
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> trafficCone() {
		return (c, p) -> p.directionalBlock(c.get(), p.models().getExistingFile(CreateBigCannons.resource("block/traffic_cone")));
	}

	public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> dropMortarShell() {
		return b -> b.properties(p -> p.noOcclusion())
			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().getExistingFile(CreateBigCannons.resource("block/drop_mortar_shell"))));
	}

	public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> dropMortarShellItem() {
		Identifier headFuzeLoc = CreateBigCannons.resource("block/drop_mortar_shell_head_fuze");
		Identifier baseFuzeLoc = CreateBigCannons.resource("block/drop_mortar_shell_base_fuze");
		return b -> b.model((c, p) -> p.blockItem(c));
	}

}
