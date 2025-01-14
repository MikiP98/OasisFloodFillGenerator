package io.github.mikip98.ofg.automation;

import io.github.mikip98.del.api.ColorExtractionAPI;
import io.github.mikip98.del.enums.AVGTypes;
import io.github.mikip98.del.structures.BlockstateWrapper;
import io.github.mikip98.del.structures.ColorRGBA;
import io.github.mikip98.del.structures.ColorReturn;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.ofg.structures.FloodFillColor;
import io.github.mikip98.ofg.structures.FloodFillFormat;

import java.util.*;

import static io.github.mikip98.ofg.OasisFloodFillGeneratorClient.LOGGER;

public class FloodFill {
    // ModId -> BlockstateId -> Set of Property value pairs
    @SuppressWarnings("rawtypes")
    Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates = new HashMap<>();
    // If 'Set<Map<SimplifiedProperty, Comparable>>' is 'null', all the blockstates automation combinations are already supported


    // Auto FloodFill format color -> ModId -> all the blockstates w properties entries
    public Map<Short, Map<String, List<String>>> floodFillEmissiveFormat1BlockEntries = new HashMap<>(); // 0 0MMR RRGG GBBB
    public Map<Short, Map<String, List<String>>> floodFillEmissiveFormat2BlockEntries = new HashMap<>(); // 0 MMMR RRGG GBBB
//    public Map<Short, Map<String, List<String>>> floodFillEmissiveFormat3BlockEntries = new HashMap<>(); // 1 RRRR GGGG BBBB

    public Map<Short, Map<String, List<String>>> floodFillEmissiveFormat1ItemEntries = new HashMap<>(); // 0 0MMR RRGG GBBB
    public Map<Short, Map<String, List<String>>> floodFillEmissiveFormat2ItemEntries = new HashMap<>(); // 0 MMMR RRGG GBBB
//    public Map<Short, Map<String, List<String>>> floodFillEmissiveFormat3ItemEntries = new HashMap<>(); // 1 RRRR GGGG BBBB

    public Map<Short, Map<String, List<String>>> floodFillTranslucentEntries = new HashMap<>(); // 0 0MMR RRGG GBBB

    // occlusion category entries; 0, 0.25, 0.50, 0.75; 1 is just ignored
    public Map<Short, Map<String, List<String>>> floodFillIgnoreEntries = new HashMap<>();


    public FloodFill() {}
    @SuppressWarnings("rawtypes")
    public FloodFill(Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates) {
        this.alreadySupportedBlockstates = alreadySupportedBlockstates;
    }


    @SuppressWarnings("rawtypes")
    public void generateFloodfillForLightEmittingBlocks(Map<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksData) {
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
                floodFillEmissiveFormat1ItemEntries.computeIfAbsent(floodFillColor.getEmissiveDataModeHSV(), k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId);
//                floodFillEmissiveFormat2ItemEntries.computeIfAbsent(floodFillColor.getEntryId(FloodFillFormat.X4096), k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId);
//                floodFillEmissiveFormat3ItemEntries.computeIfAbsent(floodFillColor.getEntryId(FloodFillFormat.X8192), k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId);

                Map<Byte, Set<Map<SimplifiedProperty, Comparable>>> propertiesData = blocksDataEntry.getValue();

                for (Map.Entry<Byte, Set<Map<SimplifiedProperty, Comparable>>> propertiesDataEntry : propertiesData.entrySet()) {
                    byte lightLevel = propertiesDataEntry.getKey();
                    Set<Map<SimplifiedProperty, Comparable>> propertySets = propertiesDataEntry.getValue();

                    List<String> blockstatesWProperties = getUnsupportedBlockstatesOfBlockstate(modId, blockstateId, propertySets);
                    if (blockstatesWProperties.isEmpty()) continue;

                    floodFillColor = new FloodFillColor(color, lightLevel);
                    short emissiveDataHSV = floodFillColor.getEmissiveDataModeHSV();

                    for (String blockstateWProperties : blockstatesWProperties) {
                        floodFillEmissiveFormat1BlockEntries.computeIfAbsent(emissiveDataHSV, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
//                        floodFillEmissiveFormat2BlockEntries.computeIfAbsent(floodFillColor.getEntryId(FloodFillFormat.X4096), k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
//                        floodFillEmissiveFormat3BlockEntries.computeIfAbsent(floodFillColor.getEntryId(FloodFillFormat.X8192), k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
                    }
                }
            }
        }
    }

    public void generateFloodfillForTranslucentBlocks(Map<String, List<String>> translucentBlocksData) {
        for (Map.Entry<String, List<String>> translucentBlocksDataEntry : translucentBlocksData.entrySet()) {
            String modId = translucentBlocksDataEntry.getKey();
            List<String> blockstateIds = translucentBlocksDataEntry.getValue();

            for (String blockstateId : blockstateIds) {
                ColorReturn colorReturn = ColorExtractionAPI.getAverageColorForBlockstate(modId, blockstateId, AVGTypes.ARITHMETIC);
                if (colorReturn == null) continue;

                List<String> blockstatesWProperties = getUnsupportedBlockstatesOfBlockstate(modId, blockstateId);
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
    }

    public void generateFloodfillForNonFullBlocks(Map<String, Map<String, Double>> nonFullBlocksData) {
        for (Map.Entry<String, Map<String, Double>> nonFullBlocksDataEntry : nonFullBlocksData.entrySet()) {
            String modId = nonFullBlocksDataEntry.getKey();
            Map<String, Double> blocksData = nonFullBlocksDataEntry.getValue();

            for (Map.Entry<String, Double> blockEntry : blocksData.entrySet()) {
                String blockstateId = blockEntry.getKey();
                List<String> blockstatesWProperties = getUnsupportedBlockstatesOfBlockstate(modId, blockstateId);
                if (blockstatesWProperties.isEmpty()) continue;

                Double volume = blockEntry.getValue();

                // Round volume to either of the categories: 0, 0.25, 0.5, 0.75, 1
                // If not 1 add to floodFillIgnoreEntries, if 1 continue
                volume = Math.round(volume * 4) / 4.0d;
                if (volume == 1.0) continue;
                Short occlusionEntryId = volume2entry.get(volume);

                for (String blockstateWProperties : blockstatesWProperties) {
                    floodFillIgnoreEntries.computeIfAbsent(occlusionEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
                }
            }
        }
        short entryId = Util.floodFillIgnoreFirstEntryId;
        LOGGER.info("Generated flood fill for non full blocks");
        LOGGER.info("block.{} = {}", entryId, prepareMessage(floodFillIgnoreEntries.get(entryId++)));
        LOGGER.info("block.{} = {}", entryId, prepareMessage(floodFillIgnoreEntries.get(entryId++)));
        LOGGER.info("block.{} = {}", entryId, prepareMessage(floodFillIgnoreEntries.get(entryId++)));
        LOGGER.info("block.{} = {}", entryId, prepareMessage(floodFillIgnoreEntries.get(entryId)));
    }
    public static String prepareMessage(Map<String, List<String>> floodFillIgnoreEntry) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : floodFillIgnoreEntry.entrySet()) {
            String modId = entry.getKey();
            List<String> blockstateIds = entry.getValue();

            stringBuilder.append(String.join(" ", blockstateIds.stream().map(blockstateId -> modId + ":" + blockstateId).toList())).append(" \\\n ");
        }

        return stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length()).toString();
    }
    @SuppressWarnings("rawtypes")
    public static String prepareFullMessage(Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> floodFillIgnoreEntry) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> entry : floodFillIgnoreEntry.entrySet()) {
            String modId = entry.getKey();
            Map<String, Set<Map<SimplifiedProperty, Comparable>>> blockstateData = entry.getValue();

            for (Map.Entry<String, Set<Map<SimplifiedProperty, Comparable>>> blockstateEntry : blockstateData.entrySet()) {
                String blockstateId = blockstateEntry.getKey();
                Set<Map<SimplifiedProperty, Comparable>> propertySets = blockstateEntry.getValue();

                for (Map<SimplifiedProperty, Comparable> propertySet : propertySets) {
                    stringBuilder.append(modId).append(":").append(blockstateId);

                    for (Map.Entry<SimplifiedProperty, Comparable> propertyEntry : propertySet.entrySet()) {
                        stringBuilder.append(":").append(propertyEntry.getKey().name).append("=").append(propertyEntry.getValue());
                    }
                    stringBuilder.append(" ");
                }
            }
            stringBuilder.append(" \\\n ");
        }

        return stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length()).toString();
    }
    public static HashMap<Double, Short> volume2entry = new HashMap<>(Map.of(
            .0, (short) 50,
            0.25, (short) 51,
            0.5, (short) 52,
            0.75, (short) 53
    ));


    @SuppressWarnings("rawtypes")
    private List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId, Set<Map<SimplifiedProperty, Comparable>> propertySets) {
        return getUnsupportedBlockstatesOfBlockstate(modId, blockstateId, propertySets, true);
    }
    @SuppressWarnings("rawtypes")
    private List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId, Set<Map<SimplifiedProperty, Comparable>> propertySets, boolean updateSupportedBlockstates) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        List<String> unsupportedBlockstatesWProperties = new ArrayList<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            // If 'null', all the blockstates automation combinations are already supported
            if (alreadySupportedBlockstates.get(modId).get(blockstateId) != null) {
                for (Map<SimplifiedProperty, Comparable> propertySet : propertySets) {
                    if (!alreadySupportedBlockstates.get(modId).get(blockstateId).contains(propertySet)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(blockstateId);
                        for (Map.Entry<SimplifiedProperty, Comparable> propertyEntry : propertySet.entrySet()) {
                            stringBuilder.append(":").append(propertyEntry.getKey().name).append("=").append(propertyEntry.getValue());
                        }
                        unsupportedBlockstatesWProperties.add(stringBuilder.toString());
                    }
                }
            }
        } else {
//            unsupportedBlockstatesWProperties.add(blockstateId);
            for (Map<SimplifiedProperty, Comparable> propertySet : propertySets) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(blockstateId);
                for (Map.Entry<SimplifiedProperty, Comparable> propertyEntry : propertySet.entrySet()) {
                    stringBuilder.append(":").append(propertyEntry.getKey().name).append("=").append(propertyEntry.getValue());
                }
                unsupportedBlockstatesWProperties.add(stringBuilder.toString());
            }
        }

        if (updateSupportedBlockstates) {
            alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>());
            Map<String, Set<Map<SimplifiedProperty, Comparable>>> modBlockstates = alreadySupportedBlockstates.get(modId);
            if (modBlockstates.containsKey(blockstateId)) {
                modBlockstates.get(blockstateId).addAll(propertySets);
            } else {
                modBlockstates.put(blockstateId, propertySets);
            }
            // TODO: Replace remove every unneeded simplified properties or replace with null if all possibilities are supported
        }
        return unsupportedBlockstatesWProperties;
    }

    private List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId) {
        return getUnsupportedBlockstatesOfBlockstate(modId, blockstateId, true);
    }
    private List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId, boolean updateSupportedBlockstates) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        List<String> unsupportedBlockstatesWProperties = new ArrayList<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            // If 'null', all the blockstates automation combinations are already supported
            if (alreadySupportedBlockstates.get(modId).get(blockstateId) != null) {
                // TODO: Add the missing logic
            }
        } else {
            unsupportedBlockstatesWProperties.add(blockstateId);
        }

        if (updateSupportedBlockstates) {
            alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, null);
        }
        return unsupportedBlockstatesWProperties;
    }
}