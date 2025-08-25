package potatowolfie.earth_and_water.item;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.custom.*;
import potatowolfie.earth_and_water.trim.ModTrimMaterials;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ModItems {
    public static final Item BORE_ROD = registerItem("bore_rod",
            new Item(new Item.Settings()
                    .registryKey(createItemRegistryKey("bore_rod"))));
    public static final Item BORE_SPAWN_EGG = registerItem("bore_spawn_egg",
            new SpawnEggItem(ModEntities.BORE,
                    new Item.Settings()
                            .registryKey(createItemRegistryKey("bore_spawn_egg"))));

    public static final Item BRINE_ROD = registerItem("brine_rod",
            new Item(new Item.Settings()
                    .registryKey(createItemRegistryKey("brine_rod"))));
    public static final Item BRINE_SPAWN_EGG = registerItem("brine_spawn_egg",
            new SpawnEggItem(ModEntities.BRINE,
                    new Item.Settings()
                            .registryKey(createItemRegistryKey("brine_spawn_egg"))));

    public static final Item STEEL_INGOT = registerItem("steel_ingot",
            new Item(new Item.Settings()
                    .registryKey(createItemRegistryKey("steel_ingot"))
                    .trimMaterial(ModTrimMaterials.STEEL)));

    public static final Item STEEL_NUGGET = registerItem("steel_nugget",
            new Item(new Item.Settings()
                    .registryKey(createItemRegistryKey("steel_nugget"))));
    public static final Item EARTH_CHARGE = registerItem("earth_charge",
            new EarthChargeItem(new Item.Settings()
                    .registryKey(createItemRegistryKey("earth_charge"))));
    public static final Item WATER_CHARGE = registerItem("water_charge",
            new WaterChargeItem(new Item.Settings()
                    .registryKey(createItemRegistryKey("water_charge"))));
    public static final Item REINFORCED_KEY = registerItem("reinforced_key",
            new Item(new Item.Settings()
                    .registryKey(createItemRegistryKey("reinforced_key"))));

    public static final Item SPIKED_SHIELD_UPGRADE_SMITHING_TEMPLATE = registerItem("spiked_shield_upgrade_smithing_template",
            new Item(new Item.Settings()
                    .registryKey(createItemRegistryKey("spiked_shield_upgrade_smithing_template"))));

    public static final ComponentType<SpikedShieldTooltipComponent> SPIKED_SHIELD_TOOLTIP = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(EarthWater.MOD_ID, "spiked_shield_tooltip"),
            ComponentType.<SpikedShieldTooltipComponent>builder()
                    .codec(SpikedShieldTooltipComponent.CODEC)
                    .packetCodec(SpikedShieldTooltipComponent.PACKET_CODEC)
                    .build()
    );

    public static final ComponentType<BattleAxeTooltipComponent> BATTLE_AXE_TOOLTIP = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(EarthWater.MOD_ID, "battle_axe_tooltip"),
            ComponentType.<BattleAxeTooltipComponent>builder()
                    .codec(BattleAxeTooltipComponent.CODEC)
                    .packetCodec(BattleAxeTooltipComponent.PACKET_CODEC)
                    .build()
    );

    public static final ComponentType<WhipTooltipComponent> WHIP_TOOLTIP = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(EarthWater.MOD_ID, "whip_tooltip"),
            ComponentType.<WhipTooltipComponent>builder()
                    .codec(WhipTooltipComponent.CODEC)
                    .packetCodec(WhipTooltipComponent.PACKET_CODEC)
                    .build()
    );

    public record SpikedShieldTooltipComponent() implements TooltipAppender {
        public static final Codec<SpikedShieldTooltipComponent> CODEC = Codec.unit(SpikedShieldTooltipComponent::new);
        public static final PacketCodec<RegistryByteBuf, SpikedShieldTooltipComponent> PACKET_CODEC = PacketCodec.unit(new SpikedShieldTooltipComponent());

        @Override
        public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.spiked_shield_upgrade_smithing_template.tooltip1"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.armor_trim_template.tooltipempty"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.armor_trim_template.tooltip1"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.spiked_shield_upgrade_smithing_template.tooltip2"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.armor_trim_template.tooltip3"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.spiked_shield_upgrade_smithing_template.tooltip3"));
        }
    }

    public record BattleAxeTooltipComponent() implements TooltipAppender {
        public static final Codec<BattleAxeTooltipComponent> CODEC = Codec.unit(BattleAxeTooltipComponent::new);
        public static final PacketCodec<RegistryByteBuf, BattleAxeTooltipComponent> PACKET_CODEC = PacketCodec.unit(new BattleAxeTooltipComponent());

        @Override
        public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.reinforced_spawner.tooltipempty"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.battle_axe.tooltip1"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.battle_axe.tooltip2"));
        }
    }

    public record WhipTooltipComponent() implements TooltipAppender {
        public static final Codec<WhipTooltipComponent> CODEC = Codec.unit(WhipTooltipComponent::new);
        public static final PacketCodec<RegistryByteBuf, WhipTooltipComponent> PACKET_CODEC = PacketCodec.unit(new WhipTooltipComponent());

        @Override
        public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.reinforced_spawner.tooltipempty"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.whip.tooltip1"));
            textConsumer.accept(Text.translatable("tooltip.earth-and-water.whip.tooltip2"));
        }
    }

    public static final Item BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("block_armor_trim_smithing_template",
            SmithingTemplateItem.of(new Item.Settings()
                    .registryKey(createItemRegistryKey("block_armor_trim_smithing_template"))));

    public static final Item GUARD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("guard_armor_trim_smithing_template",
            SmithingTemplateItem.of(new Item.Settings()
                    .registryKey(createItemRegistryKey("guard_armor_trim_smithing_template"))));

    public static final Item WHIP = registerItem("whip",
            new WhipItem(ModToolMaterials.PRISMARINE,
                    new Item.Settings().sword(ModToolMaterials.PRISMARINE, 4, -2.8F)
                            .rarity(Rarity.UNCOMMON)
                            .registryKey(createItemRegistryKey("whip"))
                            .component(WHIP_TOOLTIP, new WhipTooltipComponent())));
    public static final Item BATTLE_AXE = registerItem("battle_axe",
            new BattleAxeItem(ModToolMaterials.STEEL, 5.0F, -3.2F,
                    new Item.Settings()
                            .rarity(Rarity.UNCOMMON)
                            .registryKey(createItemRegistryKey("battle_axe"))
                            .component(BATTLE_AXE_TOOLTIP, new BattleAxeTooltipComponent()))
    );

    public static final Item SPIKED_SHIELD = Registry.register(Registries.ITEM,
            createItemRegistryKey("spiked_shield"),
            new SpikedShieldItem(new Item.Settings()
                    .maxDamage(556)
                    .component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
                    .repairable(ItemTags.WOODEN_TOOL_MATERIALS)
                    .equippableUnswappable(EquipmentSlot.OFFHAND)
                    .component(DataComponentTypes.BLOCKS_ATTACKS, new BlocksAttacksComponent(0.25F, 1.0F,
                            List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                            new BlocksAttacksComponent.ItemDamage(3.0F, 1.0F, 1.0F), Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                            Optional.of(SoundEvents.ITEM_SHIELD_BLOCK), Optional.of(SoundEvents.ITEM_SHIELD_BREAK)))
                    .component(DataComponentTypes.BREAK_SOUND, SoundEvents.ITEM_SHIELD_BREAK)
                    .registryKey(createItemRegistryKey("spiked_shield"))
            ));

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
        entries.addAfter(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, SPIKED_SHIELD_UPGRADE_SMITHING_TEMPLATE);
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
        entries.addAfter(Items.BOGGED_SPAWN_EGG, BORE_SPAWN_EGG);
        entries.addAfter(Items.BREEZE_SPAWN_EGG, BRINE_SPAWN_EGG);
    }

    public static void registerModItems() {
        EarthWater.LOGGER.info("Registering Mod Items for " + EarthWater.MOD_ID);

        // Register tooltip components for newer Fabric versions (1.21.6+)
        ComponentTooltipAppenderRegistry.addFirst(SPIKED_SHIELD_TOOLTIP);
        ComponentTooltipAppenderRegistry.addFirst(BATTLE_AXE_TOOLTIP);
        ComponentTooltipAppenderRegistry.addFirst(WHIP_TOOLTIP);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::customIngredients);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::customCombat);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::customSpawnEggs);
    }
}