package potatowolfie.earth_and_water.structure.ancient_ruins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.BiomeResolver;
import potatowolfie.earth_and_water.structure.ModStructureTypes;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class AncientRuinsStructure extends Structure {
    public static final MapCodec<AncientRuinsStructure> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                settingsCodec(instance),
                Codec.floatRange(0.0F, 1.0F).fieldOf("small_probability").forGetter((structure) -> {
                    return structure.smallProbability;
                }),
                Codec.floatRange(0.0F, 1.0F).fieldOf("medium_probability").forGetter((structure) -> {
                    return structure.mediumProbability;
                }),
                Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter((structure) -> {
                    return structure.largeProbability;
                }),
                Codec.intRange(10, 30).fieldOf("min_ruins").forGetter((structure) -> {
                    return structure.minRuins;
                }),
                Codec.intRange(10, 30).fieldOf("max_ruins").forGetter((structure) -> {
                    return structure.maxRuins;
                })
        ).apply(instance, AncientRuinsStructure::new);
    });

    public final float smallProbability;
    public final float mediumProbability;
    public final float largeProbability;
    public final int minRuins;
    public final int maxRuins;

    public AncientRuinsStructure(
            Structure.StructureSettings config,
            float smallProbability,
            float mediumProbability,
            float largeProbability,
            int minRuins,
            int maxRuins
    ) {
        super(config);
        this.smallProbability = smallProbability;
        this.mediumProbability = mediumProbability;
        this.largeProbability = largeProbability;
        this.minRuins = minRuins;
        this.maxRuins = maxRuins;
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.OCEAN_FLOOR_WG, (collector) -> {
            this.addPieces(collector, context);
        });
    }

    private void addPieces(StructurePiecesBuilder collector, Structure.GenerationContext context) {
        int attempts = 0;
        int maxAttempts = 50;

        BiomeResolver biomeResolver = context.chunkGenerator().getBiomeSource()
                .createCachingResolver(context.randomState());

        while (attempts < maxAttempts) {
            int randomY = context.random().nextInt(51) - 50;

            BlockPos testPos = new BlockPos(
                    context.chunkPos().getMiddleBlockX(),
                    randomY,
                    context.chunkPos().getMiddleBlockZ()
            );

            Holder<Biome> biome = biomeResolver.getNoiseBiome(
                    testPos.getX() >> 2,
                    testPos.getY() >> 2,
                    testPos.getZ() >> 2
            );

            if (biome.is(Biomes.DRIPSTONE_CAVES)) {
                BlockPos blockPos = new BlockPos(
                        context.chunkPos().getMinBlockX(),
                        randomY,
                        context.chunkPos().getMinBlockZ()
                );
                Rotation blockRotation = Rotation.getRandom(context.random());
                AncientRuinsGenerator.addPieces(
                        context.structureTemplateManager(),
                        blockPos,
                        blockRotation,
                        collector,
                        context.random(),
                        this
                );
                return;
            }

            attempts++;
        }
    }

    public StructureType<?> type() {
        return ModStructureTypes.ANCIENT_RUINS;
    }
}