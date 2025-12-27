package potatowolfie.earth_and_water.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.custom.*;


public class ModItems {
    public static final Item BORE_ROD = registerItem("bore_rod",
            new Item(new Item.Settings()));

    public static final Item BORE_SPAWN_EGG = registerItem("bore_spawn_egg",
            new SpawnEggItem(ModEntities.BORE, 0xffffff, 0xffffff,
                    new Item.Settings()));

    public static final Item BRINE_ROD = registerItem("brine_rod",
            new Item(new Item.Settings()));

    public static final Item BRINE_SPAWN_EGG = registerItem("brine_spawn_egg",
            new SpawnEggItem(ModEntities.BRINE, 0xffffff, 0xffffff,
                    new Item.Settings()));

    public static final Item STEEL_INGOT = registerItem("steel_ingot",
            new Item(new Item.Settings()));

    public static final Item STEEL_NUGGET = registerItem("steel_nugget",
            new Item(new Item.Settings()));

    public static final Item EARTH_CHARGE = registerItem("earth_charge",
            new EarthChargeItem(new Item.Settings()));

    public static final Item WATER_CHARGE = registerItem("water_charge",
            new WaterChargeItem(new Item.Settings()));

    public static final Item REINFORCED_KEY = registerItem("reinforced_key",
            new ReinforcedKeyItem(new Item.Settings()));

    public static final Item STEEL_UPGRADE_SMITHING_TEMPLATE = registerItem("steel_upgrade_smithing_template",
            SmithingTemplateItem.of(Identifier.of(EarthWater.MOD_ID, "steel"), FeatureFlags.VANILLA));
    public static final Item BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("block_armor_trim_smithing_template",
            SmithingTemplateItem.of(Identifier.of(EarthWater.MOD_ID, "dripstone"), FeatureFlags.VANILLA));
    public static final Item GUARD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("guard_armor_trim_smithing_template",
            SmithingTemplateItem.of(Identifier.of(EarthWater.MOD_ID, "prismarine"), FeatureFlags.VANILLA));

    public static final Item WHIP = registerItem("whip",
            new WhipItem(ModToolMaterials.PRISMARINE,
                    new Item.Settings()
                            .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.PRISMARINE, 4, -2.8F))
                            .rarity(Rarity.UNCOMMON)
            )
    );

    public static final Item BATTLE_AXE = registerItem("battle_axe",
            new BattleAxeItem(ModToolMaterials.STEEL,
                    new Item.Settings()
                            .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.STEEL, 5.0F, -3.2F))
                            .rarity(Rarity.UNCOMMON)
            ));

    public static final Item SPIKED_SHIELD = Registry.register(Registries.ITEM,
            Identifier.of(EarthWater.MOD_ID, "spiked_shield"),
            new SpikedShieldItem(new Item.Settings().maxCount(1).maxDamage(556).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT))
    );

    private static RegistryKey<Item> createItemRegistryKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EarthWater.MOD_ID, name));
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, createItemRegistryKey(name), item);
    }

    private static void customIngredients(FabricItemGroupEntries entries) {
        entries.addBefore(Items.BLAZE_ROD, BORE_ROD);
        entries.addAfter(Items.BLAZE_ROD, BRINE_ROD);
        entries.addAfter(Items.IRON_INGOT, STEEL_INGOT);
        entries.addAfter(Items.IRON_NUGGET, STEEL_NUGGET);
        entries.addAfter(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, STEEL_UPGRADE_SMITHING_TEMPLATE);
        entries.addAfter(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE);
        entries.addAfter(BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE, GUARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        entries.addAfter(Items.OMINOUS_TRIAL_KEY, REINFORCED_KEY);
    }

    private static void customCombat(FabricItemGroupEntries entries) {
        entries.addAfter(Items.TRIDENT, WHIP);
        entries.addAfter(WHIP, BATTLE_AXE);
        entries.addAfter(Items.SHIELD, SPIKED_SHIELD);
        entries.addAfter(Items.WIND_CHARGE, WATER_CHARGE);
        entries.addAfter(WATER_CHARGE, EARTH_CHARGE);
    }

    private static void customSpawnEggs(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BREEZE_SPAWN_EGG, BORE_SPAWN_EGG);
        entries.addAfter(BORE_SPAWN_EGG, BRINE_SPAWN_EGG);
    }

    public static void registerModItems() {
        EarthWater.LOGGER.info("Registering Mod Items for " + EarthWater.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::customIngredients);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::customCombat);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::customSpawnEggs);
    }
}