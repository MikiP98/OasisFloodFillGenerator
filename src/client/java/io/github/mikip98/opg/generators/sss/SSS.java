package io.github.mikip98.opg.generators.sss;

import io.github.mikip98.del.api.BlockstatesAPI;
import net.minecraft.block.*;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class SSS {

    public static void generateSSS() {
        Set<Class<?>> classesOfInterest = Set.of(
                LeavesBlock.class,              // All leaves
                TallPlantBlock.class,           // Tall plants (2 blocks), CAUTION: Extends 'PlantBlock'!
                PlantBlock.class,               // All plants, INCLUDING TALL PLANTS!!!
                AbstractBannerBlock.class,      // Wall and Floor Banners
                AbstractPlantPartBlock.class    // E.G. Kelp; SSS but no waving
        );

        Map<Class<?>, Map<String, List<String>>> dataOfInterest = BlockstatesAPI.getChildBlockstatesOfClasses(classesOfInterest);

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
