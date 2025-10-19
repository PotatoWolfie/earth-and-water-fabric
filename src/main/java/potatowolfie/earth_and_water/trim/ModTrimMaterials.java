package potatowolfie.earth_and_water.trim;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProvidesTrimMaterialComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrimAssets;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import potatowolfie.earth_and_water.EarthWater;

import java.util.Optional;

public class ModTrimMaterials {
    public static final RegistryKey<ArmorTrimMaterial> STEEL = RegistryKey.of(RegistryKeys.TRIM_MATERIAL,
            Identifier.of(EarthWater.MOD_ID, "steel"));

    public static void bootstrap(Registerable<ArmorTrimMaterial> registry) {
        register(registry, STEEL, Style.EMPTY.withColor(11520457), ModTrimAssets.STEEL);
    }

    public static Optional<RegistryEntry<ArmorTrimMaterial>> get(RegistryWrapper.WrapperLookup registries, ItemStack stack) {
        ProvidesTrimMaterialComponent providesTrimMaterialComponent = (ProvidesTrimMaterialComponent)stack.get(DataComponentTypes.PROVIDES_TRIM_MATERIAL);
        return providesTrimMaterialComponent != null ? providesTrimMaterialComponent.getMaterial(registries) : Optional.empty();
    }

    private static void register(Registerable<ArmorTrimMaterial> registry, RegistryKey<ArmorTrimMaterial> key, Style style, ArmorTrimAssets assets) {
        Text text = Text.translatable(Util.createTranslationKey("trim_material", key.getValue())).fillStyle(style);
        registry.register(key, new ArmorTrimMaterial(assets, text));
    }

    private static RegistryKey<ArmorTrimMaterial> of(String id) {
        return RegistryKey.of(RegistryKeys.TRIM_MATERIAL, Identifier.ofVanilla(id));
    }
}