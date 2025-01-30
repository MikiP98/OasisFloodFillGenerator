package io.github.mikip98.opg.generation.sss;

import io.github.mikip98.del.api.BlockstatesAPI;
import io.github.mikip98.opg.config.Config;
import io.github.mikip98.opg.enums.SSSTypes;
import io.github.mikip98.opg.generation.Controller;
import io.github.mikip98.opg.objects.SSSSupportIntermediate;
import net.minecraft.block.*;

import java.util.*;

public class SSSGenerator {

    public static SSSSupportIntermediate generateSSS(Controller controller) {
        final Set<Class<?>> classesOfInterest = Set.of(
                AbstractBannerBlock.class,      // Wall and Floor Banners
                AbstractPlantPartBlock.class,   // E.G. Kelp; SSS but no waving
                GrassBlock.class,               // Grass
                LeavesBlock.class,              // All leaves
                MushroomPlantBlock.class,       // Mushrooms, CAUTION: Extends 'PlantBlock'!
                PlantBlock.class,               // All plants, INCLUDING TALL PLANTS & MUSHROOMS!!!
                TallPlantBlock.class            // Tall plants (2 blocks), CAUTION: Extends 'PlantBlock'!
        );
        Map<Class<?>, Map<String, List<String>>> dataOfInterest = BlockstatesAPI.getChildBlockstatesOfClasses(classesOfInterest);

        Map<String, List<String>> plants = dataOfInterest.get(PlantBlock.class);
        Map<String, List<String>> mushrooms = dataOfInterest.get(MushroomPlantBlock.class);
        Map<String, List<String>> tallPlants = dataOfInterest.get(TallPlantBlock.class);

        Map<String, List<String>> otherPlants = new HashMap<>();  // plants - (mushrooms + tallPlants)

        for (Map.Entry<String, List<String>> entry : plants.entrySet()) {
            String modId = entry.getKey();
            List<String> blockstateIds = entry.getValue();
            if (mushrooms.containsKey(modId)) {
                for (String blockstateId : blockstateIds) {
                    if (!mushrooms.get(modId).contains(blockstateId)) {
                        otherPlants.computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId);
                    }
                }
            } else if (tallPlants.containsKey(modId)) {
                for (String blockstateId : blockstateIds) {
                    if (!tallPlants.get(modId).contains(blockstateId)) {
                        otherPlants.computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId);
                    }
                }
            } else {
                otherPlants.put(modId, blockstateIds);
            }
        }

        dataOfInterest.put(PlantBlock.class, otherPlants);
//        dataOfInterest.remove(MushroomPlantBlock.class);
//        dataOfInterest.remove(TallPlantBlock.class);

        Map<Short, Map<String, List<String>>> SSSSupportEntries = new HashMap<>();

        for (Map.Entry<Class<?>, Map<String, List<String>>> entry : dataOfInterest.entrySet()) {
            Class<?> clazz = entry.getKey();
            SSSTypes category = Config.MCClass2SSSCategory.get(clazz);
            short entryId = Config.SSSCategory2EntryId.get(category);
            Map<String, List<String>> blockstateData = entry.getValue();

//            SSSSupportEntries.computeIfAbsent(entryId, k -> new HashMap<>()).putAll(blockstateData);

            for (Map.Entry<String, List<String>> entry2 : blockstateData.entrySet()) {
                String modId = entry2.getKey();
                List<String> blockstateIds = entry2.getValue();

                for (String blockstateId : blockstateIds) {
                    List<String> blockstateWProperties = controller.getNotSupportedBlockstates(modId, blockstateId);
                    SSSSupportEntries.computeIfAbsent(entryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).addAll(blockstateWProperties);
                }
            }
        }

        short tallPlantLowerEntryId = Config.SSSCategory2EntryId.get(SSSTypes.TALL_PLANT_LOWER);
        short tallPlantUpperEntryId = Config.SSSCategory2EntryId.get(SSSTypes.TALL_PLANT_UPPER);

        for (Map.Entry<String, List<String>> entry : SSSSupportEntries.get(tallPlantLowerEntryId).entrySet()) {
            String modId = entry.getKey();
            List<String> blockstateIds = new ArrayList<>(entry.getValue());
            for (String blockstateId : blockstateIds) {
                SSSSupportEntries.get(tallPlantLowerEntryId).get(modId).remove(blockstateId);
                SSSSupportEntries.get(tallPlantLowerEntryId).get(modId).add(blockstateId + ":half=lower");
                SSSSupportEntries.computeIfAbsent(tallPlantUpperEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId + ":half=upper");
            }
        }


        return new SSSSupportIntermediate(
                SSSSupportEntries
        );
    }
}
