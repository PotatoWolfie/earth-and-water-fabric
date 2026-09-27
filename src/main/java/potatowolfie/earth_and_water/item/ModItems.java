package potatowolfie.earth_and_water.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.custom.*;
import potatowolfie.earth_and_water.trim.ModTrimMaterials;

import java.util.List;
import java.util.Optional;

public class ModItems {
    public static final Item BORE_ROD = registerItem("bore_rod",
            new Item(new Item.Properties()
                    .setId(createItemRegistryKey("bore_rod"))));
    public static final Item BORE_SPAWN_EGG = registerItem("bore_spawn_egg",
            new SpawnEggItem(
                    new Item.Properties().spawnEgg(ModEntities.BORE)
                            .setId(createItemRegistryKey("bore_spawn_egg"))));

    public static final Item BRINE_ROD = registerItem("brine_rod",
            new Item(new Item.Properties()
                    .setId(createItemRegistryKey("brine_rod"))));
    public static final Item BRINE_SPAWN_EGG = registerItem("brine_spawn_egg",
            new SpawnEggItem(
                    new Item.Properties().spawnEgg(ModEntities.BRINE)
                            .setId(createItemRegistryKey("brine_spawn_egg"))));

    public static final Item STEEL_INGOT = registerItem("steel_ingot",
            new Item(new Item.Properties()
                    .setId(createItemRegistryKey("steel_ingot"))
                    .trimMaterial(ModTrimMaterials.STEEL)));

    public static final Item STEEL_NUGGET = registerItem("steel_nugget",
            new Item(new Item.Properties()
                    .setId(createItemRegistryKey("steel_nugget"))));
    public static final Item EARTH_CHARGE = registerItem("earth_charge",
            new EarthChargeItem(new Item.Properties()
                    .setId(createItemRegistryKey("earth_charge"))));
    public static final Item WATER_CHARGE = registerItem("water_charge",
            new WaterChargeItem(new Item.Properties()
                    .setId(createItemRegistryKey("water_charge"))));
    public static final Item REINFORCED_KEY = registerItem("reinforced_key",
            new ReinforcedKeyItem(new Item.Properties()
                    .setId(createItemRegistryKey("reinforced_key"))));

    public static final Item STEEL_UPGRADE_SMITHING_TEMPLATE = registerItem("steel_upgrade_smithing_template",
            SmithingTemplateItem.createArmorTrimTemplate(new Item.Properties().rarity(Rarity.UNCOMMON)
                    .setId(createItemRegistryKey("steel_upgrade_smithing_template"))));

    public static final Item BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("block_armor_trim_smithing_template",
            SmithingTemplateItem.createArmorTrimTemplate(new Item.Properties().rarity(Rarity.UNCOMMON)
                    .setId(createItemRegistryKey("block_armor_trim_smithing_template"))));

    public static final Item GUARD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("guard_armor_trim_smithing_template",
            SmithingTemplateItem.createArmorTrimTemplate(new Item.Properties().rarity(Rarity.UNCOMMON)
                    .setId(createItemRegistryKey("guard_armor_trim_smithing_template"))));

    public static final Item WHIP = registerItem("whip",
            new WhipItem(ModToolMaterials.PRISMARINE,
                    new Item.Properties().sword(ModToolMaterials.PRISMARINE, 4, -2.8F)
                            .rarity(Rarity.UNCOMMON)
                            .setId(createItemRegistryKey("whip"))
            ));
    public static final Item BATTLE_AXE = registerItem("battle_axe",
            new BattleAxeItem(new Item.Properties()
                            .axe(ModToolMaterials.STEEL, 5.0F, -3.2F)
                            .rarity(Rarity.UNCOMMON)
                            .setId(createItemRegistryKey("battle_axe"))
    ));

    public static final Item SPIKED_SHIELD = Registry.register(
            BuiltInRegistries.ITEM,
            createItemRegistryKey("spiked_shield"),
            new SpikedShieldItem(new Item.Properties()
                    .durability(556)
                    .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
                    .repairable(ItemTags.WOODEN_TOOL_MATERIALS)
                    .equippableUnswappable(EquipmentSlot.OFFHAND)
                    .delayedComponent(DataComponents.BLOCKS_ATTACKS, (context) ->
                            new BlocksAttacks(
                                    0.25F,
                                    1.0F,
                                    List.of(new BlocksAttacks.DamageReduction(
                                            90.0F,
                                            Optional.empty(),
                                            0.0F,
                                            1.0F
                                    )),
                                    new BlocksAttacks.ItemDamageFunction(
                                            3.0F,
                                            1.0F,
                                            1.0F
                                    ),
                                    Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                                    Optional.of(SoundEvents.SHIELD_BLOCK),
                                    Optional.of(SoundEvents.SHIELD_BREAK)
                            )
                    )
                    .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
                    .setId(createItemRegistryKey("spiked_shield"))
            )
    );

    private static ResourceKey<Item> createItemRegistryKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, name));
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, createItemRegistryKey(name), item);
    }

    public static void registerModItems() {
        EarthWater.LOGGER.info("Registering Mod Items for " + EarthWater.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(output -> {

                    output.insertBefore(Items.BLAZE_ROD, List.of(
                            new ItemStack(BORE_ROD)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.BLAZE_ROD, List.of(
                            new ItemStack(BRINE_ROD)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.IRON_INGOT, List.of(
                            new ItemStack(STEEL_INGOT)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.IRON_NUGGET, List.of(
                            new ItemStack(STEEL_NUGGET)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, List.of(
                            new ItemStack(STEEL_UPGRADE_SMITHING_TEMPLATE)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, List.of(
                            new ItemStack(BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE),
                            new ItemStack(GUARD_ARMOR_TRIM_SMITHING_TEMPLATE)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.OMINOUS_TRIAL_KEY, List.of(
                            new ItemStack(REINFORCED_KEY)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register(output -> {

                    output.insertAfter(Items.TRIDENT, List.of(
                            new ItemStack(WHIP),
                            new ItemStack(BATTLE_AXE)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.SHIELD, List.of(
                            new ItemStack(SPIKED_SHIELD)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.WIND_CHARGE, List.of(
                            new ItemStack(WATER_CHARGE),
                            new ItemStack(EARTH_CHARGE)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(output -> {

                    output.insertAfter(Items.BREEZE_SPAWN_EGG, List.of(
                            new ItemStack(BORE_SPAWN_EGG),
                            new ItemStack(BRINE_SPAWN_EGG)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
    }
}