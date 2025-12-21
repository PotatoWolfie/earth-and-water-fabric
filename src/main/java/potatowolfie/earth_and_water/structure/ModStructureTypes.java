package potatowolfie.earth_and_water.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.structure.ancient_ruins.AncientRuinsStructure;
import potatowolfie.earth_and_water.structure.conduit_monument.ConduitMonumentStructure;

public class ModStructureTypes {
    public static final StructureType<ConduitMonumentStructure> CONDUIT_MONUMENT =
            register("conduit_monument", ConduitMonumentStructure.CODEC);

    public static final StructureType<AncientRuinsStructure> ANCIENT_RUINS =
            register("ancient_ruins", AncientRuinsStructure.CODEC);

    private static <S extends Structure> StructureType<S> register(
            String id,
            MapCodec<S> codec
    ) {
        return Registry.register(
                Registries.STRUCTURE_TYPE,
                Identifier.of(EarthWater.MOD_ID, id),
                () -> codec
        );
    }

    public static void registerStructureTypes() {
        EarthWater.LOGGER.info("Registering structure types for " + EarthWater.MOD_ID);
    }
}