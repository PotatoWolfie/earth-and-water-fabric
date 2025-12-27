package potatowolfie.earth_and_water.trim;

import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.item.ModItems;

import java.util.Map;

public class ModTrimMaterials {
    public static final RegistryKey<ArmorTrimMaterial> STEEL = RegistryKey.of(RegistryKeys.TRIM_MATERIAL,
            Identifier.of(EarthWater.MOD_ID, "steel"));

    public static void bootstrap(Registerable<ArmorTrimMaterial> registerable) {
        register(registerable, STEEL, Registries.ITEM.getEntry(ModItems.STEEL_INGOT), Style.EMPTY.withColor(TextColor.parse("#afc9c9").getOrThrow()),
                0.2f);
    }

    private static void register(Registerable<ArmorTrimMaterial> registerable, RegistryKey<ArmorTrimMaterial> armorTrimKey, RegistryEntry<Item> item, Style style,
                                 float itemModelIndex) {
        ArmorTrimMaterial trimmaterial = new ArmorTrimMaterial(armorTrimKey.getValue().getPath(), item, itemModelIndex, Map.of(),
                Text.translatable(Util.createTranslationKey("trim_material", armorTrimKey.getValue())).fillStyle(style));

        registerable.register(armorTrimKey, trimmaterial);
    }
}