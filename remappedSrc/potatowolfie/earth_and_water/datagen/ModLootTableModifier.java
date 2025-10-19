package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.item.ModItems;

public class ModLootTableModifier {

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (key.getValue().equals(Identifier.of("minecraft", "chests/pillager_outpost"))) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(0.0f, 1.0f))
                        .with(ItemEntry.builder(ModItems.STEEL_NUGGET)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 8.0f))));
                tableBuilder.pool(poolBuilder);
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (key.getValue().equals(Identifier.of("minecraft/datapacks/trade_rebalance/data/minecraft/loot_table/chests/pillager_outpost"))) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(0.0f, 1.0f))
                        .with(ItemEntry.builder(ModItems.STEEL_NUGGET)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 8.0f))));
                tableBuilder.pool(poolBuilder);
            }
        });
    }
}