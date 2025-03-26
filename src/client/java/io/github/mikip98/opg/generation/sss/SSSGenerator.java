package io.github.mikip98.opg.generation.sss;

import io.github.mikip98.del.api.BlockstatesAPI;
import io.github.mikip98.opg.config.Config;
import io.github.mikip98.opg.enums.SSSTypes;
import io.github.mikip98.opg.generation.Controller;
import net.minecraft.block.*;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class SSSGenerator {

    public static SSSSupportIntermediate generateSSS(Controller controller) {
        Map<Class<?>, Map<String, List<String>>> vanillaBlocks = getVanillaBlocks();

        Map<String, List<String>> plants = vanillaBlocks.get(PlantBlock.class);  // Later replaced with otherPlants
        Map<String, List<String>> mushrooms = vanillaBlocks.get(MushroomPlantBlock.class);
        Map<String, List<String>> tallPlants = vanillaBlocks.get(TallPlantBlock.class);

        Map<Class<?>, Map<String, List<String>>> bopBlocks = getBOPBlocks();

        Map<String, List<String>> leafPiles = null;
        if (bopBlocks != null) {
            leafPiles = bopBlocks.get(biomesoplenty.block.LeafPileBlock.class);
            for (String modId : leafPiles.keySet()) {
                vanillaBlocks.get(AbstractPlantPartBlock.class).computeIfAbsent(modId, k -> new ArrayList<>()).addAll(leafPiles.get(modId));
            }
        }

        Map<String, List<String>> otherPlants = new HashMap<>();  // plants - (mushrooms + tallPlants)
        for (Map.Entry<String, List<String>> entry : plants.entrySet()) {
            String modId = entry.getKey();
            List<String> blockstateIds = entry.getValue();

            List<String> blockToFilterOut = new ArrayList<>();
            if (mushrooms.containsKey(modId)) blockToFilterOut.addAll(mushrooms.get(modId));
            if (tallPlants.containsKey(modId)) blockToFilterOut.addAll(tallPlants.get(modId));
            if (leafPiles != null && leafPiles.containsKey(modId)) blockToFilterOut.addAll(leafPiles.get(modId));

            if (!blockToFilterOut.isEmpty())
                blockstateIds = blockstateIds.stream().filter(blockstateId -> !blockToFilterOut.contains(blockstateId)).toList();

            otherPlants.put(modId, blockstateIds);
        }

        vanillaBlocks.put(PlantBlock.class, otherPlants);

        // EntryId -> ModId -> BlockstateId -> UnsupportedPropertySets
        Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> SSSSupportEntries = new HashMap<>();

        for (Map.Entry<Class<?>, Map<String, List<String>>> entry : vanillaBlocks.entrySet()) {
            Class<?> clazz = entry.getKey();
            SSSTypes category = Config.MCClass2SSSCategory.get(clazz);
            short entryId = Config.SSSCategory2EntryId.get(category);
            Map<String, List<String>> blockstateData = entry.getValue();

//            SSSSupportEntries.computeIfAbsent(entryId, k -> new HashMap<>()).putAll(blockstateData);

            for (Map.Entry<String, List<String>> entry2 : blockstateData.entrySet()) {
                String modId = entry2.getKey();
                List<String> blockstateIds = entry2.getValue();

                for (String blockstateId : blockstateIds) {
                    Set<Map<String, Comparable<?>>> blockstateWProperties = controller.getNotSupportedBlockstates(modId, blockstateId);
                    if (blockstateWProperties == null) continue;
                    if (blockstateWProperties.isEmpty()) blockstateWProperties = null;
                    SSSSupportEntries.computeIfAbsent(entryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, blockstateWProperties);
                }
            }
        }

//        short tallPlantLowerEntryId = Config.SSSCategory2EntryId.get(SSSTypes.TALL_PLANT_LOWER);
//        short tallPlantUpperEntryId = Config.SSSCategory2EntryId.get(SSSTypes.TALL_PLANT_UPPER);
//
//        for (Map.Entry<String, Map<String, Set<Map<String, Comparable<?>>>>> entry : SSSSupportEntries.get(tallPlantLowerEntryId).entrySet()) {
//            String modId = entry.getKey();
//            Map<String, Set<Map<String, Comparable<?>>>> blockstates = entry.getValue();
//
//            for (String blockstateId : blockstates.keySet()) {
//                Set<Map<String, Comparable<?>>> halfLowerProperties;
//                Set<Map<String, Comparable<?>>> halfUpperProperties;
//                Set<Map<String, Comparable<?>>> blockstateWProperties = blockstates.get(blockstateId);
//                if (blockstateWProperties == null) {
//                    halfLowerProperties = new HashSet<>(Set.of(new HashMap<>(Map.of("half", "lower"))));
//                    halfUpperProperties = new HashSet<>(Set.of(new HashMap<>(Map.of("half", "upper"))));
//                } else {
//                    halfLowerProperties = new HashSet<>(blockstateWProperties.size());
//                    halfUpperProperties = new HashSet<>(blockstateWProperties.size());
//
//                    for (Map<String, Comparable<?>> propertySet : blockstateWProperties) {
//                        propertySet.put("half", "lower");
//                        halfLowerProperties.add(new HashMap<>(propertySet));
//                        propertySet.put("half", "upper");
//                        halfUpperProperties.add(new HashMap<>(propertySet));
//                    }
//                }
//
//                SSSSupportEntries.get(tallPlantLowerEntryId).get(modId).put(blockstateId, halfLowerProperties);
//                SSSSupportEntries.computeIfAbsent(tallPlantUpperEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, halfUpperProperties);
//
////                SSSSupportEntries.get(tallPlantLowerEntryId).get(modId).remove(blockstateId);
////                SSSSupportEntries.get(tallPlantLowerEntryId).get(modId).add(blockstateId + ":half=lower");
////                SSSSupportEntries.computeIfAbsent(tallPlantUpperEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, blockstateId + ":half=upper");
//            }
//        }


        return new SSSSupportIntermediate(
                SSSSupportEntries
        );
    }

    protected static Map<Class<?>, Map<String, List<String>>> getVanillaBlocks() {
        final Set<Class<?>> classesOfInterest = Set.of(
                AbstractBannerBlock.class,      // Wall and Floor Banners
                AbstractPlantPartBlock.class,   // E.G. Kelp; SSS but no waving
                GrassBlock.class,               // Grass
                LeavesBlock.class,              // All leaves
                MushroomPlantBlock.class,       // Mushrooms, CAUTION: Extends 'PlantBlock'!
                PlantBlock.class,               // All plants, INCLUDING TALL PLANTS & MUSHROOMS!!!
                TallPlantBlock.class            // Tall plants (2 blocks), CAUTION: Extends 'PlantBlock'!
        );
        return BlockstatesAPI.getChildBlockstatesOfClasses(classesOfInterest);
    }

    protected static Map<Class<?>, Map<String, List<String>>> getBOPBlocks() {
        try {
            final Set<Class<?>> classesOfInterest = Set.of(
                    biomesoplenty.block.LeafPileBlock.class
            );
            return BlockstatesAPI.getChildBlockstatesOfClasses(classesOfInterest);

        } catch (NoClassDefFoundError e) {
            LOGGER.warn("Biomes O' Plenty blocks classes were not found. You can ignore it if you don't use this mod.");
            return null;
        }
    }
}
