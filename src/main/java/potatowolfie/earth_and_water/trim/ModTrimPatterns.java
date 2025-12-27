package potatowolfie.earth_and_water.trim;

import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.item.ModItems;

public class ModTrimPatterns {
    public static final RegistryKey<ArmorTrimPattern> BLOCK = RegistryKey.of(RegistryKeys.TRIM_PATTERN, Identifier.of(EarthWater.MOD_ID, "block"));
    public static final RegistryKey<ArmorTrimPattern> GUARD = RegistryKey.of(RegistryKeys.TRIM_PATTERN, Identifier.of(EarthWater.MOD_ID, "guard"));

    public static void bootstrap(Registerable<ArmorTrimPattern> context) {
        register(context, ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE, BLOCK);
        register(context, ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE, GUARD);
    }

    private static void register(Registerable<ArmorTrimPattern> context, Item item, RegistryKey<ArmorTrimPattern> key) {
        ArmorTrimPattern trimPattern = new ArmorTrimPattern(key.getValue(), Registries.ITEM.getEntry(item),
                Text.translatable(Util.createTranslationKey("trim_pattern", key.getValue())), false);
        context.register(key, trimPattern);
    }
}