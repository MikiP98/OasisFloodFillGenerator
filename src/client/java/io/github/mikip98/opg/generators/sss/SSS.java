package io.github.mikip98.opg.generators.sss;

import io.github.mikip98.del.api.BlockstatesAPI;
import io.github.mikip98.del.structures.SimplifiedProperty;
import net.minecraft.block.*;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class SSS {

    @SuppressWarnings("rawtypes")
    public SSS (
            Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates,
            Map<String, Long> newSupportStats
    ) {}

    public static void generateSSS() {
        final Set<Class<?>> classesOfInterest = Set.of(
                AbstractBannerBlock.class,      // Wall and Floor Banners
                AbstractPlantPartBlock.class,   // E.G. Kelp; SSS but no waving
                LeavesBlock.class,              // All leaves
                MushroomPlantBlock.class,       // Mushrooms, CAUTION: Extends 'PlantBlock'!
                PlantBlock.class,               // All plants, INCLUDING TALL PLANTS & MUSHROOMS!!!
                TallPlantBlock.class            // Tall plants (2 blocks), CAUTION: Extends 'PlantBlock'!
        );
        Map<Class<?>, Map<String, List<String>>> dataOfInterest = BlockstatesAPI.getChildBlockstatesOfClasses(classesOfInterest);

        Map<String, List<String>> banners = dataOfInterest.get(AbstractBannerBlock.class);

        Map<String, List<String>> leaves = dataOfInterest.get(LeavesBlock.class);

        Map<String, List<String>> plants = dataOfInterest.get(PlantBlock.class);
        Map<String, List<String>> mushrooms = dataOfInterest.get(MushroomPlantBlock.class);
        Map<String, List<String>> tallPlants = dataOfInterest.get(TallPlantBlock.class);
        Map<String, List<String>> otherPlants;  // plants - (mushrooms + tallPlants)

        Map<String, List<String>> plantParts = dataOfInterest.get(AbstractPlantPartBlock.class);

//        Map<String, List<String>> leavesData = dataOfInterest.get(LeavesBlock.class);
//        List<String> modIds = new ArrayList<>(leavesData.keySet());
//        Collections.sort(modIds);
//        for (String modId : modIds) {
//            List<String> blockstateIds = leavesData.get(modId);
//            LOGGER.info(String.join(" ", blockstateIds.stream().map(blockstateId -> modId + ":" + blockstateId).toList()));
//        }

        for (Map.Entry<Class<?>, Map<String, List<String>>> entry : dataOfInterest.entrySet()) {
            Class<?> clazz = entry.getKey();
            LOGGER.info(clazz.getSimpleName());
            Map<String, List<String>> blockstateData = entry.getValue();
            List<String> modIds = new ArrayList<>(blockstateData.keySet());
            Collections.sort(modIds);
            for (String modId : modIds) {
                LOGGER.info("- {}", modId);
                List<String> blockstateIds = blockstateData.get(modId);
                LOGGER.info("  - {}", String.join(" ", blockstateIds.stream().map(blockstateId -> modId + ":" + blockstateId).toList()));
            }
        }
    }
}
