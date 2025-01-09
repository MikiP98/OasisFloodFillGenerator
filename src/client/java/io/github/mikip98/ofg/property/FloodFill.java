package io.github.mikip98.ofg.property;

import java.util.*;

public class FloodFill {
    // ModId -> BlockstateId -> Set of Property value pairs
    @SuppressWarnings("rawtypes")
    Map<String, Map<String, Set<Map<String, Comparable>>>> alreadySupportedBlockstates;

    // Auto FloodFill format color -> all the blockstates w properties entries
    Map<Short, List<String>> floodFillColourBlockEntries;
    Map<Short, List<String>> floodFillColourItemEntries;

    // occlusion category entries; 0, 0.25, 0.50, 0.75; 1 is just ignored
    Map<Integer, List<String>> floodFillIgnoreEntries;


    @SuppressWarnings("rawtypes")
    public void generateFloodfillForLightEmittingBlocks(Map<String, Map<String, Map<Byte, Set<Map<String, Comparable>>>>> lightEmittingBlocksData) {

    }

    public void generateFloodfillForTranslucentBlocks(Map<String, List<String>> translucentBlocksData) {

    }

    public void generateFloodfillForNonFullBlocks(Map<String, Map<String, Double>> nonFullBlocksData) {
        for (Map.Entry<String, Map<String, Double>> nonFullBlocksDataEntry : nonFullBlocksData.entrySet()) {
            String modId = nonFullBlocksDataEntry.getKey();
            Map<String, Double> blocksData = nonFullBlocksDataEntry.getValue();

            for (Map.Entry<String, Double> blockEntry : blocksData.entrySet()) {
                String blockstateId = blockEntry.getKey();
                if (isBlockstateSupported(modId, blockstateId)) continue;
                Double volume = blockEntry.getValue();

                // Round volume to either of the categories: 0, 0.25, 0.5, 0.75, 1
                // If not 1 add to floodFillIgnoreEntries, if 1 continue
                volume = Math.round(volume * 4) / 4.0d;
                if (volume == 1.0) continue;

                Integer occlusionEntryId = volume2entry.get(volume);
                floodFillIgnoreEntries.computeIfAbsent(occlusionEntryId, k -> new ArrayList<>()).add(blockstateId);
            }
        }
    }
    public static HashMap<Double, Integer> volume2entry = new HashMap<>(Map.of(
            .0, 50,
            0.25, 51,
            0.5, 52,
            0.75, 53
    ));


    // TODO: Rework this system so if a blockstate with selected properties is supported, return the blockstate with other properties

    @SuppressWarnings("rawtypes")
    private boolean isBlockstateSupported(String modId, String blockstate, Map<String, Comparable> properties) {
        return alreadySupportedBlockstates.get(modId).get(blockstate).contains(properties);
    }
    private boolean isBlockstateSupported(String modId, String blockstate) {
        return alreadySupportedBlockstates.get(modId).containsKey(blockstate);
    }
}