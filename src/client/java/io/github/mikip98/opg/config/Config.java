package io.github.mikip98.opg.config;

import io.github.mikip98.opg.enums.SSSTypes;
import net.minecraft.block.*;

import java.util.Map;

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

    public static final Map<SSSTypes, Short> SSSCategory2EntryId = Map.of(
            SSSTypes.TALL_PLANT_LOWER,  (short) 13,  // TallPlantBlock.class
            SSSTypes.TALL_PLANT_UPPER,  (short) 14,  // TallPlantBlock.class
            SSSTypes.GROUND_WAVING,     (short) 54,  // PlantBlock.class (- mushrooms) (- tall plants)
            SSSTypes.AIR_WAVING,        (short) 56,  // LeavesBlock.class
            SSSTypes.STRONG,            (short) 80,  // AbstractPlantPartBlock.class
            SSSTypes.WEAK,              (short) 81,  // MushroomPlantBlock.class
            SSSTypes.WEAK_3,            (short) 83,  // AbstractBannerBlock.class
            SSSTypes.GRASS,             (short) 85   // GrassBlock.class
    );
    public static final Map<Class<?>, SSSTypes> MCClass2SSSCategory = Map.of(
            PlantBlock.class,               SSSTypes.GROUND_WAVING,
            LeavesBlock.class,              SSSTypes.AIR_WAVING,
            AbstractPlantPartBlock.class,   SSSTypes.STRONG,
            MushroomPlantBlock.class,       SSSTypes.WEAK,
            AbstractBannerBlock.class,      SSSTypes.WEAK_3,
            GrassBlock.class,               SSSTypes.GRASS
            // 'TallPlantBlock.class' is not included as it's type is both 'SSSTypes.TALL_PLANT_LOWER' and 'SSSTypes.TALL_PLANT_UPPER'
            // Because of that it can't be configured right now
    );
}
