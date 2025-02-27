package io.github.mikip98.opg.config;

import io.github.mikip98.opg.enums.SSSTypes;
import net.minecraft.block.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Config {

    // --- PIPELINE ---
    // Pipeline is currently located in 'src/client/java/io/github/mikip98/opg/generators/MainGenerator.java'


    // --- FLOOD FILL ---

    // Boundaries for Auto FloodFill colors support, used if '#ifdef AUTO_GENERATED_FLOODFILL' is not found  TODO: implement
    public static final short autoFloodFillMinValue = 512;
    public static final short autoFloodFillMaxValue = 8191;

    public static final short floodFillIgnoreFirstEntryId = 50;
    public static final byte floodFillIgnoreEntryCount = 4;
    // Main FloodFill ignore entries will be calculated in the following way:
    // 1.0 / floodFillIgnoreEntryCount = IgnoreDelta
    // Entry: (floodFillIgnoreFirstEntryId)      ->  occlusion: (0.0)
    // Entry: (floodFillIgnoreFirstEntryId + 1)  ->  occlusion: (IgnoreDelta)
    // Entry: (floodFillIgnoreFirstEntryId + 2)  ->  occlusion: (IgnoreDelta * 2)
    // ...
    // Entry: (floodFillIgnoreFirstEntryId + floodFillIgnoreEntryCount - 1)  ->  occlusion: (1.0 - IgnoreDelta)


    // --- SSS ---

    public static final short ID_SSS_TALL_PLANT_LOWER = 13;
    public static final short ID_SSS_TALL_PLANT_UPPER = 14;
    public static final short ID_SSS_CEILING_WAVING = 17;
    public static final short ID_SSS_GROUND_WAVING = 54;
    public static final short ID_SSS_AIR_WAVING = 56;
    public static final short ID_SSS_STRONG = 80;
    public static final short ID_SSS_WEAK = 81;
    public static final short ID_SSS_WEAK_3 = 83;
    public  static final short ID_SSS_GRASS = 85;

    public static final Set<Short> mainSSSEntryIds = Set.of(
            ID_SSS_CEILING_WAVING,
            ID_SSS_GROUND_WAVING,
            ID_SSS_AIR_WAVING,
            ID_SSS_STRONG,
            ID_SSS_WEAK,
            ID_SSS_WEAK_3,
            ID_SSS_GRASS
    );
    public static final Set<Short> specialSSSEntryIds = Set.of(
            ID_SSS_TALL_PLANT_LOWER,
            ID_SSS_TALL_PLANT_UPPER
    );

    public static final Map<SSSTypes, Short> SSSCategory2EntryId = Map.of(
            SSSTypes.TALL_PLANT_LOWER,  ID_SSS_TALL_PLANT_LOWER,  // TallPlantBlock.class
            SSSTypes.TALL_PLANT_UPPER,  ID_SSS_TALL_PLANT_UPPER,  // TallPlantBlock.class
            SSSTypes.CEILING_WAVING,    ID_SSS_CEILING_WAVING,    // HangingRootsBlock.class
            SSSTypes.GROUND_WAVING,     ID_SSS_GROUND_WAVING,     // PlantBlock.class (- mushrooms) (- tall plants)
            SSSTypes.AIR_WAVING,        ID_SSS_AIR_WAVING,        // LeavesBlock.class
            SSSTypes.STRONG,            ID_SSS_STRONG,            // AbstractPlantPartBlock.class
            SSSTypes.WEAK,              ID_SSS_WEAK,              // MushroomPlantBlock.class
            SSSTypes.WEAK_3,            ID_SSS_WEAK_3,            // AbstractBannerBlock.class
            SSSTypes.GRASS,             ID_SSS_GRASS              // GrassBlock.class
    );
    public static final Map<Class<?>, SSSTypes> MCClass2SSSCategory = Map.of(
            HangingRootsBlock.class,        SSSTypes.CEILING_WAVING,
            PlantBlock.class,               SSSTypes.GROUND_WAVING,
            LeavesBlock.class,              SSSTypes.AIR_WAVING,
            AbstractPlantPartBlock.class,   SSSTypes.STRONG,
            MushroomPlantBlock.class,       SSSTypes.WEAK,
            AbstractBannerBlock.class,      SSSTypes.WEAK_3,
            GrassBlock.class,               SSSTypes.GRASS,
            // 'TallPlantBlock.class' is later split into 'SSSTypes.TALL_PLANT_LOWER' and 'SSSTypes.TALL_PLANT_UPPER'
            // Here only the lower part is added for it to be processed
            TallPlantBlock.class,           SSSTypes.TALL_PLANT_LOWER
    );


    // --- DEBUG ---

    public static final boolean DEBUG = true;
}
