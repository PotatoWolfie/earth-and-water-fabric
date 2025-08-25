package potatowolfie.earth_and_water.world.biome;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import potatowolfie.earth_and_water.block.ModBlocks;

public class ModMaterialRules {
    private static final MaterialRules.MaterialRule STONE = makeStateRule(Blocks.STONE);
    private static final MaterialRules.MaterialRule DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
    private static final MaterialRules.MaterialRule DRIPSTONE_BLOCK = makeStateRule(Blocks.DRIPSTONE_BLOCK);
    private static final MaterialRules.MaterialRule DARK_DRIPSTONE_BLOCK = makeStateRule(ModBlocks.DARK_DRIPSTONE_BLOCK);

    public static MaterialRules.MaterialRule makeDarkDripstoneCavesRules() {
        return MaterialRules.condition(MaterialRules.biome(ModBiomes.DARK_DRIPSTONE_CAVES),
                MaterialRules.sequence(
                        MaterialRules.condition(
                                MaterialRules.verticalGradient("deepslate", YOffset.fixed(-64), YOffset.fixed(0)),
                                DEEPSLATE
                        ),
                        STONE
                )
        );
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}