package potatowolfie.earth_and_water.structure.ancient_ruins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import potatowolfie.earth_and_water.structure.ModStructureTypes;

import java.util.Optional;

public class AncientRuinsStructure extends Structure {
    public static final MapCodec<AncientRuinsStructure> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                configCodecBuilder(instance),
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
            Structure.Config config,
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

    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        return getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, (collector) -> {
            this.addPieces(collector, context);
        });
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        int attempts = 0;
        int maxAttempts = 50;

        while (attempts < maxAttempts) {
            int randomY = context.random().nextInt(51) - 50;

            BlockPos testPos = new BlockPos(
                    context.chunkPos().getCenterX(),
                    randomY,
                    context.chunkPos().getCenterZ()
            );

            RegistryEntry<Biome> biome = context.chunkGenerator().getBiomeSource().getBiome(
                    testPos.getX() >> 2,
                    testPos.getY() >> 2,
                    testPos.getZ() >> 2,
                    context.noiseConfig().getMultiNoiseSampler()
            );

            if (biome.matchesKey(BiomeKeys.DRIPSTONE_CAVES)) {
                BlockPos blockPos = new BlockPos(
                        context.chunkPos().getStartX(),
                        randomY,
                        context.chunkPos().getStartZ()
                );
                BlockRotation blockRotation = BlockRotation.random(context.random());
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

    public StructureType<?> getType() {
        return ModStructureTypes.ANCIENT_RUINS;
    }
}