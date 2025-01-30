package io.github.mikip98.opg.generation.floodfill;

import io.github.mikip98.del.api.BlockstatesAPI;
import io.github.mikip98.del.api.ColorExtractionAPI;
import io.github.mikip98.del.enums.AVGTypes;
import io.github.mikip98.del.structures.BlockstateWrapper;
import io.github.mikip98.del.structures.ColorRGBA;
import io.github.mikip98.del.structures.ColorReturn;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.config.Config;
import io.github.mikip98.opg.generation.Controller;
import io.github.mikip98.opg.objects.FloodFillColor;
import io.github.mikip98.opg.structures.FloodFillGeneralSupport;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class FloodFillGeneralGenerator {

    protected final Controller controller;

//    // Auto FloodFill format color -> ModId -> all the blockstates w properties entries
//    public Map<Short, Map<String, List<String>>> floodFillEmissiveBlockEntries = new HashMap<>();
//
//    public Map<Short, Map<String, List<String>>> floodFillEmissiveItemEntries = new HashMap<>();
//
//    public Map<Short, Map<String, List<String>>> floodFillTranslucentEntries = new HashMap<>(); // 0 0MMR RRGG GBBB
//
//    // occlusion category entries; 0, 0.25, 0.50, 0.75; 1 is just ignored
//    public Map<Short, Map<String, List<String>>> floodFillIgnoreEntries = new HashMap<>();

    public FloodFillGeneralSupport floodFillGeneralSupport;


    public FloodFillGeneralGenerator(Controller controller, FloodFillGeneralSupport floodFillGeneralSupport) {
        this.controller = controller;
        this.floodFillGeneralSupport = floodFillGeneralSupport;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public void generateFloodfillForLightEmittingBlocks() {
        Map<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksData = BlockstatesAPI.getLightEmittingBlocksData();

        int blockCount = 0;
        for (Map.Entry<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksDataEntry : lightEmittingBlocksData.entrySet()) {
            for (Map.Entry<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>> blocksDataEntry : lightEmittingBlocksDataEntry.getValue().entrySet()) {
                for (Map.Entry<Byte, Set<Map<SimplifiedProperty, Comparable>>> propertiesDataEntry : blocksDataEntry.getValue().entrySet()) {
                    blockCount += propertiesDataEntry.getValue().size();
                }
            }
        }
        LOGGER.info("Generating flood fill for {} light emitting blocks", blockCount);

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
                floodFillGeneralSupport.lightEmittingSupport
                        .computeIfAbsent(floodFillColor.getEmissiveDataModeHSV(), k -> new HashMap<>())
                        .computeIfAbsent(modId, k -> new HashMap<>())
                        .computeIfAbsent(blockstateId, k -> new HashSet<>());

                Map<Byte, Set<Map<SimplifiedProperty, Comparable>>> propertiesData = blocksDataEntry.getValue();

                for (Map.Entry<Byte, Set<Map<SimplifiedProperty, Comparable>>> propertiesDataEntry : propertiesData.entrySet()) {
                    byte lightLevel = propertiesDataEntry.getKey();
                    Set<Map<SimplifiedProperty, Comparable>> propertySets = propertiesDataEntry.getValue();

                    Set<Map<String, Comparable<?>>> unsupportedPropertySets = controller.getNotSupportedBlockstates(modId, blockstateId, (Set) propertySets);
                    if (unsupportedPropertySets == null || unsupportedPropertySets.isEmpty()) continue;  // TODO: Why isEmpty()?

                    floodFillColor = new FloodFillColor(color, lightLevel);
                    short emissiveDataHSV = floodFillColor.getEmissiveDataModeHSV();

//                    if (blockstateId.equals("orange_lamp")) {
//                        LOGGER.info("orange_lamp; Color: {}; EmissiveData: {}", color, emissiveDataHSV);
//                    }

                    floodFillGeneralSupport.lightEmittingSupport
                            .computeIfAbsent(emissiveDataHSV, k -> new HashMap<>())
                            .computeIfAbsent(modId, k -> new HashMap<>())
                            .put(blockstateId, unsupportedPropertySets);
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

                floodFillGeneralSupport.translucentSupport
                        .computeIfAbsent(tintData, k -> new HashMap<>())
                        .computeIfAbsent(modId, k -> new HashMap<>())
                        .put(blockstateId, null);
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
//                    floodFillSupportIntermediate.mainNonFullSupport.computeIfAbsent(occlusionEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
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
}