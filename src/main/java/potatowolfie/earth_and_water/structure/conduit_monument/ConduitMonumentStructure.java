package potatowolfie.earth_and_water.structure.conduit_monument;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import potatowolfie.earth_and_water.structure.ModStructureTypes;

import java.util.Optional;

public class ConduitMonumentStructure extends Structure {
    public static final MapCodec<ConduitMonumentStructure> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                configCodecBuilder(instance),
                Codec.floatRange(0.0F, 1.0F).fieldOf("medium_probability").forGetter((structure) -> {
                    return structure.mediumProbability;
                }),
                Codec.intRange(2, 5).fieldOf("min_ruins").forGetter((structure) -> {
                    return structure.minRuins;
                }),
                Codec.intRange(2, 5).fieldOf("max_ruins").forGetter((structure) -> {
                    return structure.maxRuins;
                })
        ).apply(instance, ConduitMonumentStructure::new);
    });

    public final float mediumProbability;
    public final int minRuins;
    public final int maxRuins;

    public ConduitMonumentStructure(Structure.Config config, float mediumProbability, int minRuins, int maxRuins) {
        super(config);
        this.mediumProbability = mediumProbability;
        this.minRuins = minRuins;
        this.maxRuins = maxRuins;
    }

    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        return getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, (collector) -> {
            this.addPieces(collector, context);
        });
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(context.random());
        ConduitMonumentGenerator.addPieces(
                context.structureTemplateManager(),
                blockPos,
                blockRotation,
                collector,
                context.random(),
                this
        );
    }

    public StructureType<?> getType() {
        return ModStructureTypes.CONDUIT_MONUMENT;
    }
}