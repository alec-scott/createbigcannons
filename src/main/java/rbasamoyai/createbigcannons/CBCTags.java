package rbasamoyai.createbigcannons;




import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import rbasamoyai.createbigcannons.utils.CBCRegistryUtils;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class CBCTags {

	public static class CBCBlockTags {
		public static final TagKey<Block>
			THICK_TUBING = makeTag("thick_tubing"),
			REDUCES_SPREAD = makeTag("reduces_spread"),
			DRILL_CAN_PASS_THROUGH = makeTag("drill_can_pass_through"),
		// Datagen tags
		OBSIDIAN = commonTag("obsidian", "obsidian", "obsidian" /* No Fabric c: tag */),
			SANDSTONE = commonTag("sandstone", "sandstone", "sandstone"),
			CONCRETE = commonTag("concrete", "concrete", "concrete"),
			NETHERRACK = commonTag("netherrack", "netherrack", "netherrack"),
			SPARK_EFFECT_ON_IMPACT = makeTag("spark_effect_on_impact"),
			SPLINTER_EFFECT_ON_IMPACT = makeTag("splinter_effect_on_impact"),
			GLASS_EFFECT_ON_IMPACT = makeTag("glass_effect_on_impact");

		public static TagKey<Block> makeTag(String path) {
			TagKey<Block> tag = TagKey.create(CBCRegistryUtils.getBlockRegistryKey(), CreateBigCannons.resource(path));
			return tag;
		}

		public static TagKey<Block> commonTag(String mainPath, String forgePath, String fabricPath) {
			TagKey<Block> mainTag = makeTag(mainPath);
			return mainTag;
		}

	}

	public static class CBCItemTags {

        // Crafting tags
		public static final TagKey<Item>
			IMPACT_FUZE_HEAD = makeTag("impact_fuze_head"),
			GUNPOWDER = commonTag("gunpowder", "gunpowders"),
			GEMS_QUARTZ = commonTag("gems_quartz", "gems/quartz"),
			DUSTS_REDSTONE = commonTag("dusts_redstone", "dusts/redstone"),
			STONE = commonTag("stone", "stones"),
			DUST_GLOWSTONE = commonTag("dust_glowstone", "dusts/glowstone"),
			GLASS = commonTag("glass", "glass_blocks"),
			INEXPENSIVE_BIG_CARTRIDGE_SHEET = makeTag("inexpensive_big_cartridge_sheet"),
			NITROPOWDER = makeTag("nitropowder"),
			BIG_CANNON_PROPELLANT = makeTag("big_cannon_propellant"),
			BIG_CANNON_PROPELLANT_BAGS = makeTag("big_cannon_propellant_bags"),
			BIG_CANNON_CARTRIDGES = makeTag("big_cannon_cartridges"),
			BIG_CANNON_PROJECTILES = makeTag("big_cannon_projectiles"),
			AUTOCANNON_AMMO_CONTAINERS = makeTag("autocannon_ammo_containers"),
			FUZES = makeTag("fuzes"),
			SPENT_AUTOCANNON_CASINGS = makeTag("spent_autocannon_casings"),
			AUTOCANNON_CARTRIDGES = makeTag("autocannon_cartridges"),
			AUTOCANNON_ROUNDS = makeTag("autocannon_rounds"),
			GUNPOWDER_PINCH = makeTag("gunpowder_pinch"),
			GUNCOTTON = makeTag("guncotton"),
			CAN_BE_NITRATED = makeTag("can_be_nitrated"),
			HIGH_EXPLOSIVE_MATERIALS = makeTag("high_explosive_materials"),
			NITRO_ACIDIFIERS = makeTag("nitro_acidifiers"),
			GELATINIZERS = makeTag("gelatinizers"),
			GAS_MASKS = makeTag("gas_masks");

		public static TagKey<Item> makeTag(String loc) {
			TagKey<Item> tag = CBCRegistryUtils.createItemTag(CreateBigCannons.resource(loc));
			return tag;
		}

		public static TagKey<Item> commonTag(String mainPath, String path) {
			TagKey<Item> mainTag = makeTag(mainPath);
			return mainTag;
		}

	}

	public static class CBCFluidTags {
		public static final TagKey<Fluid>
			MOLTEN_METAL = makeTag("molten_metal");

		public static TagKey<Fluid> makeTag(String loc) {
			TagKey<Fluid> tag = TagKey.create(CBCRegistryUtils.getFluidRegistryKey(), CreateBigCannons.resource(loc));
			return tag;
		}
	}

	/**
	 * PORT-NOTE(fabric): tag contents come from the checked-in {@code src/generated/resources} JSON. The datagen
	 * providers that wrote them (Registrate tag appenders, Create palette/copper sets) are not ported.
	 */
	public static void register() {
	}

	private static Identifier alloyed(String path) {
		return CBCUtils.location("alloyed", path);
	}

	private static Identifier createdeco(String path) {
		return CBCUtils.location("createdeco", path);
	}

}
