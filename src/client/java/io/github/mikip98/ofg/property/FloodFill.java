package io.github.mikip98.ofg.property;

import io.github.mikip98.del.api.ColorExtractionAPI;
import io.github.mikip98.del.structures.ColorRGBA;
import io.github.mikip98.del.structures.ColorReturn;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.ofg.structures.FloodFillColor;

import java.util.*;

import static io.github.mikip98.ofg.OasisFloodFillGeneratorClient.LOGGER;

public class FloodFill {
    // ModId -> BlockstateId -> Set of Property value pairs
    @SuppressWarnings("rawtypes")
    Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates = new HashMap<>();
    // If 'Set<Map<SimplifiedProperty, Comparable>>' is 'null', all the blockstates property combinations are already supported


    // Auto FloodFill format color -> ModId -> all the blockstates w properties entries
    Map<Short, Map<String, List<String>>> floodFillEmissiveFormat1BlockEntries = new HashMap<>(); // 0 0MMR RRGG GBBB
    Map<Short, Map<String, List<String>>> floodFillEmissiveFormat2BlockEntries = new HashMap<>(); // 0 MMMR RRGG GBBB
    Map<Short, Map<String, List<String>>> floodFillEmissiveFormat3BlockEntries = new HashMap<>(); // 1 RRRR GGGG BBBB

    Map<Short, Map<String, List<String>>> floodFillEmissiveFormat1ItemEntries = new HashMap<>(); // 0 0MMR RRGG GBBB
    Map<Short, Map<String, List<String>>> floodFillEmissiveFormat2ItemEntries = new HashMap<>(); // 0 MMMR RRGG GBBB
    Map<Short, Map<String, List<String>>> floodFillEmissiveFormat3ItemEntries = new HashMap<>(); // 1 RRRR GGGG BBBB

    Map<Short, Map<String, List<String>>> floodFillTranslucentEntries = new HashMap<>(); // 0 0MMR RRGG GBBB

    // occlusion category entries; 0, 0.25, 0.50, 0.75; 1 is just ignored
    Map<Integer, Map<String, List<String>>> floodFillIgnoreEntries = new HashMap<>();


    @SuppressWarnings("rawtypes")
    public void generateFloodfillForLightEmittingBlocks(Map<String, Map<String, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksData) {

    }

    public void generateFloodfillForTranslucentBlocks(Map<String, List<String>> translucentBlocksData) {
        for (Map.Entry<String, List<String>> translucentBlocksDataEntry : translucentBlocksData.entrySet()) {
            String modId = translucentBlocksDataEntry.getKey();
            List<String> blockstateIds = translucentBlocksDataEntry.getValue();

            for (String blockstateId : blockstateIds) {
                ColorReturn colorReturn = ColorExtractionAPI.getAverageColorForBlockstate(modId, blockstateId);
                if (colorReturn == null) continue;

                List<String> blockstatesWProperties = getUnsupportedBlockstatesOfBlockstate(modId, blockstateId);
                if (blockstatesWProperties.isEmpty()) continue;

                ColorRGBA color = colorReturn.color_avg;
//                color.multiply(1 - color.a);

                FloodFillColor floodFillColor = new FloodFillColor(color);

                for (String blockstateWProperties : blockstatesWProperties) {

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
                Integer occlusionEntryId = volume2entry.get(volume);

                for (String blockstateWProperties : blockstatesWProperties) {
                    floodFillIgnoreEntries.computeIfAbsent(occlusionEntryId, k -> new HashMap<>()).computeIfAbsent(modId, k -> new ArrayList<>()).add(blockstateWProperties);
                }
            }
        }
        int entryId = Util.floodFillIgnoreFirstEntryId;
        LOGGER.info("Generated flood fill for non full blocks");
        LOGGER.info("block.{} = {}", entryId++, prepareMessage(floodFillIgnoreEntries.get(50)));
        LOGGER.info("block.{} = {}", entryId++, prepareMessage(floodFillIgnoreEntries.get(51)));
        LOGGER.info("block.{} = {}", entryId++, prepareMessage(floodFillIgnoreEntries.get(52)));
        LOGGER.info("block.{} = {}", entryId, prepareMessage(floodFillIgnoreEntries.get(53)));
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


    private List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId) {
        return getUnsupportedBlockstatesOfBlockstate(modId, blockstateId, true);
    }
    private List<String> getUnsupportedBlockstatesOfBlockstate(String modId, String blockstateId, boolean updateSupportedBlockstates) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        List<String> unsupportedBlockstatesWProperties = new ArrayList<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            // If 'null', all the blockstates property combinations are already supported
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