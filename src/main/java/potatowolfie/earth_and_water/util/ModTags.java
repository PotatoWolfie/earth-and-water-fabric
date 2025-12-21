package potatowolfie.earth_and_water.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.StructureType;
import potatowolfie.earth_and_water.EarthWater;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_STEEL_TOOL = createTag("needs_steel_tool");
        public static final TagKey<Block> INCORRECT_FOR_STEEL_TOOL = createTag("incorrect_for_steel_tool");
        public static final TagKey<Block> NEEDS_PRISMARINE_TOOL = createTag("needs_prismarine_tool");
        public static final TagKey<Block> INCORRECT_FOR_PRISMARINE_TOOL = createTag("incorrect_for_prismarine_tool");
        public static final TagKey<Block> IS_REINFORCED_SPAWNER = createTag("is_reinforced_spawner");
        public static final TagKey<Block> E_W_CONDUIT_FRAME_BLOCKS = createTag("e_w_conduit_frame_blocks");
        public static final TagKey<Block> HAS_A_DRIPSTONE_BLOCK = createTag("has_a_dripstone_block");
        public static final TagKey<Block> HAS_A_DARK_DRIPSTONE_BLOCK = createTag("has_a_dark_dripstone_block");
        public static final TagKey<Block> HAS_A_PRISMARINE_BLOCK = createTag("has_a_prismarine_block");
        public static final TagKey<Block> HAS_A_DARK_PRSIMARINE_BLOCK = createTag("has_a_dark_prismarine_block");
        public static final TagKey<Block> HAS_STEEL = createTag("has_steel");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(EarthWater.MOD_ID, name));
        }
    }

    public static class Item {
        public static final TagKey<net.minecraft.item.Item> BATTLE_AXE = createTag("battle_axe");
        public static final TagKey<net.minecraft.item.Item> BATTLE_AXE_ENCHANTABLE = createTag("battle_axe_enchantable");
        public static final TagKey<net.minecraft.item.Item> SPIKED_SHIELD = createTag("spiked_shield");
        public static final TagKey<net.minecraft.item.Item> STEEL_REPAIR = createTag("steel_repair");
        public static final TagKey<net.minecraft.item.Item> PRISMARINE_REPAIR = createTag("prismarine_repair");

        private static TagKey<net.minecraft.item.Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(EarthWater.MOD_ID, name));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> CONDUIT_IMMUNE = TagKey.of(
                RegistryKeys.ENTITY_TYPE, Identifier.of("earth-and-water", "conduit_immune"));
        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(EarthWater.MOD_ID, name));
        }
    }

    public static class Structures {
        public static final TagKey<StructureType<?>> IS_ANCIENT_RUINS = createTag("is_ancient_ruins");

        private static TagKey<StructureType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.STRUCTURE_TYPE, Identifier.of(EarthWater.MOD_ID, name));
        }
    }
}