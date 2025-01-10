package io.github.mikip98.ofg.property;

import io.github.mikip98.del.structures.SimplifiedProperty;

import java.util.*;

import static io.github.mikip98.ofg.OasisFloodFillGeneratorClient.LOGGER;

public class FloodFill {
    // ModId -> BlockstateId -> Set of Property value pairs
    @SuppressWarnings("rawtypes")
    Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates = new HashMap<>();


    // Auto FloodFill format color -> all the blockstates w properties entries
    Map<Short, List<String>> floodFillColourBlockEntries = new HashMap<>();
    Map<Short, List<String>> floodFillColourItemEntries = new HashMap<>();

    // occlusion category entries; 0, 0.25, 0.50, 0.75; 1 is just ignored
    Map<Integer, Map<String, List<String>>> floodFillIgnoreEntries = new HashMap<>();


    @SuppressWarnings("rawtypes")
    public void generateFloodfillForLightEmittingBlocks(Map<String, Map<String, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksData) {

    }

    public void generateFloodfillForTranslucentBlocks(Map<String, List<String>> translucentBlocksData) {

    }

    public void generateFloodfillForNonFullBlocks(Map<String, Map<String, Double>> nonFullBlocksData) {
        for (Map.Entry<String, Map<String, Double>> nonFullBlocksDataEntry : nonFullBlocksData.entrySet()) {
            String modId = nonFullBlocksDataEntry.getKey();
            Map<String, Double> blocksData = nonFullBlocksDataEntry.getValue();

            for (Map.Entry<String, Double> blockEntry : blocksData.entrySet()) {
                String blockstateId = blockEntry.getKey();
//                if (isBlockstateSupported(modId, blockstateId)) continue; // TODO: Bring this back
                Double volume = blockEntry.getValue();

                // Round volume to either of the categories: 0, 0.25, 0.5, 0.75, 1
                // If not 1 add to floodFillIgnoreEntries, if 1 continue
                volume = Math.round(volume * 4) / 4.0d;
                if (volume == 1.0) continue;

                Integer occlusionEntryId = volume2entry.get(volume);
                floodFillIgnoreEntries.computeIfAbsent(occlusionEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateId);
            }
        }
        LOGGER.info("Generated flood fill for non full blocks");
        LOGGER.info("block.50 = {}", prepareMessage(floodFillIgnoreEntries.get(50)));
        LOGGER.info("block.51 = {}", prepareMessage(floodFillIgnoreEntries.get(51)));
        LOGGER.info("block.52 = {}", prepareMessage(floodFillIgnoreEntries.get(52)));
        LOGGER.info("block.53 = {}", prepareMessage(floodFillIgnoreEntries.get(53)));
    }
    private static String prepareMessage(Map<String, List<String>> floodFillIgnoreEntry) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : floodFillIgnoreEntry.entrySet()) {
            String modId = entry.getKey();
            List<String> blockstateIds = entry.getValue();

            stringBuilder.append(String.join(" ", blockstateIds.stream().map(blockstateId -> modId + ":" + blockstateId).toList())).append(" \\\n ");
        }

        return stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length()).toString();
    }
    public static HashMap<Double, Integer> volume2entry = new HashMap<>(Map.of(
            .0, 50,
            0.25, 51,
            0.5, 52,
            0.75, 53
    ));


    // TODO: Rework this system so if a blockstate with selected properties is supported, return the blockstate with other properties

    @SuppressWarnings("rawtypes")
    private static List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId) {
        return new ArrayList<>();
    }
}