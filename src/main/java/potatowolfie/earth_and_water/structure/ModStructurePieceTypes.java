package potatowolfie.earth_and_water.structure;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.structure.ancient_ruins.AncientRuinsGenerator;
import potatowolfie.earth_and_water.structure.conduit_monument.ConduitMonumentGenerator;

public class ModStructurePieceTypes {
    public static final StructurePieceType CONDUIT_MONUMENT =
            register((StructurePieceType.ManagerAware) ConduitMonumentGenerator.Piece::new, "conduit_monument");

    public static final StructurePieceType ANCIENT_RUINS =
            register((StructurePieceType.ManagerAware) AncientRuinsGenerator.Piece::new, "ancient_ruins");

    private static StructurePieceType register(StructurePieceType type, String id) {
        return Registry.register(Registries.STRUCTURE_PIECE,
                Identifier.of("earth-and-water", id), type);
    }

    private static StructurePieceType register(
            String id,
            StructurePieceType.Simple type
    ) {
        return Registry.register(
                Registries.STRUCTURE_PIECE,
                Identifier.of(EarthWater.MOD_ID, id),
                type
        );
    }

    public static void registerStructurePieceTypes() {
        EarthWater.LOGGER.info("Registering structure piece types for " + EarthWater.MOD_ID);
    }
}