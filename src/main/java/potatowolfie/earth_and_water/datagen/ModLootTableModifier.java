package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import potatowolfie.earth_and_water.item.ModItems;

public class ModLootTableModifier {

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (key.identifier().equals(Identifier.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"))) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ContextIntProviders.between(0, 1))
                        .add(LootItem.lootTableItem(ModItems.STEEL_NUGGET)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8))));
                tableBuilder.withPool(poolBuilder);
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (key.identifier().equals(Identifier.parse("minecraft/datapacks/trade_rebalance/data/minecraft/loot_table/chests/pillager_outpost"))) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ContextIntProviders.between(0, 1))
                        .add(LootItem.lootTableItem(ModItems.STEEL_NUGGET)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))));
                tableBuilder.withPool(poolBuilder);
            }
        });
    }
}