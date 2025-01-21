package io.github.mikip98.opg.generators.floodfill;

import io.github.mikip98.del.api.BlockstatesAPI;
import io.github.mikip98.del.api.ColorExtractionAPI;
import io.github.mikip98.del.enums.AVGTypes;
import io.github.mikip98.del.structures.BlockstateWrapper;
import io.github.mikip98.del.structures.ColorRGBA;
import io.github.mikip98.del.structures.ColorReturn;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.config.Config;
import io.github.mikip98.opg.generators.Controller;
import io.github.mikip98.opg.structures.FloodFillColor;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class FloodFill {

    protected final Controller controller;

    // Auto FloodFill format color -> ModId -> all the blockstates w properties entries
    public Map<Short, Map<String, List<String>>> floodFillEmissiveBlockEntries = new HashMap<>();

    public Map<Short, Map<String, List<String>>> floodFillEmissiveItemEntries = new HashMap<>();

    public Map<Short, Map<String, List<String>>> floodFillTranslucentEntries = new HashMap<>(); // 0 0MMR RRGG GBBB

    // occlusion category entries; 0, 0.25, 0.50, 0.75; 1 is just ignored
    public Map<Short, Map<String, List<String>>> floodFillIgnoreEntries = new HashMap<>();


    public FloodFill(Controller controller) {
        this.controller = controller;
    }


    @SuppressWarnings("rawtypes")
    public void generateFloodfillForLightEmittingBlocks() {
        Map<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksData = BlockstatesAPI.getLightEmittingBlocksData();

        for (Map.Entry<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksDataEntry : lightEmittingBlocksData.entrySet()) {
            String modId = lightEmittingBlocksDataEntry.getKey();
            Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>> blocksData = lightEmittingBlocksDataEntry.getValue();

            for (Map.Entry<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>> blocksDataEntry : blocksData.entrySet()) {
                BlockstateWrapper blockstateWrapper = blocksDataEntry.getKey();
                String blockstateId = blockstateWrapper.blockstateId;
                ColorReturn colorReturn = ColorExtractionAPI.getAverageColorForBlockstate(modId, blockstateId);

                if (colorReturn == null) continue;
                ColorRGBA color = colorReturn.color_avg;

                FloodFillColor floodFillColor = new FloodFillColor(color, blockstateWrapper.defaultEmission);
                floodFillEmissiveItemEntries.computeIfAbsent(floodFillColor.getEmissiveDataModeHSV(), k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId);

                Map<Byte, Set<Map<SimplifiedProperty, Comparable>>> propertiesData = blocksDataEntry.getValue();

                for (Map.Entry<Byte, Set<Map<SimplifiedProperty, Comparable>>> propertiesDataEntry : propertiesData.entrySet()) {
                    byte lightLevel = propertiesDataEntry.getKey();
                    Set<Map<SimplifiedProperty, Comparable>> propertySets = propertiesDataEntry.getValue();

                    List<String> blockstatesWProperties = getUnsupportedBlockstatesOfBlockstate(modId, blockstateId, propertySets);
                    if (blockstatesWProperties.isEmpty()) continue;

                    floodFillColor = new FloodFillColor(color, lightLevel);
                    short emissiveDataHSV = floodFillColor.getEmissiveDataModeHSV();

                    if (blockstateId.equals("orange_lamp")) {
                        LOGGER.info("orange_lamp; Color: {}; EmissiveData: {}", color, emissiveDataHSV);
                    }

                    for (String blockstateWProperties : blockstatesWProperties) {
                        floodFillEmissiveBlockEntries.computeIfAbsent(emissiveDataHSV, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
                    }
                }
            }
        }
        LOGGER.info("Generated flood fill for light emitting blocks");
    }

    public void generateFloodfillForTranslucentBlocks() {
        Map<String, List<String>> translucentBlocksData = BlockstatesAPI.getTranslucentBlockNames();

        for (Map.Entry<String, List<String>> translucentBlocksDataEntry : translucentBlocksData.entrySet()) {
            String modId = translucentBlocksDataEntry.getKey();
            List<String> blockstateIds = translucentBlocksDataEntry.getValue();

            for (String blockstateId : blockstateIds) {
                ColorReturn colorReturn = ColorExtractionAPI.getAverageColorForBlockstate(modId, blockstateId, AVGTypes.ARITHMETIC);
                if (colorReturn == null) continue;

                List<String> blockstatesWProperties = controller.getNotSupportedBlockstates(modId, blockstateId);
                if (blockstatesWProperties.isEmpty()) continue;

                ColorRGBA color = colorReturn.color_avg;
                double max = color.getMaxRGB();
                double multiplier = 1 / max;
                color.multiply(multiplier);
//                color.multiply(1 - color.a);

                FloodFillColor floodFillColor = new FloodFillColor(color);
                short tintData = floodFillColor.getTintData();

                for (String blockstateWProperties : blockstatesWProperties) {
                    floodFillTranslucentEntries.computeIfAbsent(tintData, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
                }
            }
        }
        LOGGER.info("Generated flood fill for translucent blocks");
    }

    public void generateFloodfillForNonFullBlocks() {
        Map<String, Map<String, Double>> nonFullBlocksData = BlockstatesAPI.getNonFullBlocks().knownNonFullBlocksData;

        HashMap<Double, Short> volume2entry = generateVolume2entry();

        for (Map.Entry<String, Map<String, Double>> nonFullBlocksDataEntry : nonFullBlocksData.entrySet()) {
            String modId = nonFullBlocksDataEntry.getKey();
            Map<String, Double> blocksData = nonFullBlocksDataEntry.getValue();

            for (Map.Entry<String, Double> blockEntry : blocksData.entrySet()) {
                String blockstateId = blockEntry.getKey();
                List<String> blockstatesWProperties = controller.getNotSupportedBlockstates(modId, blockstateId);
                if (blockstatesWProperties.isEmpty()) continue;

                double volume = blockEntry.getValue();

                // Round volume to either of the categories: 0, 0.25, 0.5, 0.75, 1
                // If not 1 add to floodFillIgnoreEntries, if 1 or above continue
                volume = (double) Math.round(volume * Config.floodFillIgnoreEntryCount) / Config.floodFillIgnoreEntryCount;
                if (volume >= 1.0) continue;
                short occlusionEntryId = volume2entry.get(volume);

                for (String blockstateWProperties : blockstatesWProperties) {
                    floodFillIgnoreEntries.computeIfAbsent(occlusionEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
                }
            }
        }
        LOGGER.info("Generated flood fill for non full blocks");
    }
    protected static HashMap<Double, Short> generateVolume2entry() {
        HashMap<Double, Short> volume2entry = new HashMap<>();
        for (int i = 0; i < Config.floodFillIgnoreEntryCount; i++) {
            double volume = (double) i / Config.floodFillIgnoreEntryCount;
            volume2entry.put(volume, (short) (Config.floodFillIgnoreFirstEntryId + i));
        }
        return volume2entry;
    }


    @SuppressWarnings("rawtypes")
    protected List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId, Set<Map<SimplifiedProperty, Comparable>> propertySets) {
        return getUnsupportedBlockstatesOfBlockstate(modId, blockstateId, propertySets, true);
    }
    @SuppressWarnings("rawtypes")
    protected List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId, Set<Map<SimplifiedProperty, Comparable>> propertySets, boolean updateSupportedBlockstates) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        List<String> unsupportedBlockstatesWProperties = new ArrayList<>();

//        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
//            // If 'null', all the blockstates automation combinations are already supported
//            if (alreadySupportedBlockstates.get(modId).get(blockstateId) != null) {
//                for (Map<SimplifiedProperty, Comparable> propertySet : propertySets) {
//                    if (!alreadySupportedBlockstates.get(modId).get(blockstateId).contains(propertySet)) {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        stringBuilder.append(blockstateId);
//                        for (Map.Entry<SimplifiedProperty, Comparable> propertyEntry : propertySet.entrySet()) {
//                            stringBuilder.append(":").append(propertyEntry.getKey().name).append("=").append(propertyEntry.getValue());
//                        }
//                        unsupportedBlockstatesWProperties.add(stringBuilder.toString());
//                    }
//                }
//            }
//        } else {
////            unsupportedBlockstatesWProperties.add(blockstateId);
//            for (Map<SimplifiedProperty, Comparable> propertySet : propertySets) {
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(blockstateId);
//                for (Map.Entry<SimplifiedProperty, Comparable> propertyEntry : propertySet.entrySet()) {
//                    stringBuilder.append(":").append(propertyEntry.getKey().name).append("=").append(propertyEntry.getValue());
//                }
//                unsupportedBlockstatesWProperties.add(stringBuilder.toString());
//            }
//        }
//
//        if (updateSupportedBlockstates) {
//            alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>());
//            Map<String, Set<Map<SimplifiedProperty, Comparable>>> modBlockstates = alreadySupportedBlockstates.get(modId);
//            if (modBlockstates.containsKey(blockstateId)) {
//                modBlockstates.get(blockstateId).addAll(propertySets);
//            } else {
//                modBlockstates.put(blockstateId, propertySets);
//            }
//            // TODO: Replace remove every unneeded simplified properties or replace with null if all possibilities are supported
//        }
        return unsupportedBlockstatesWProperties;
    }
}