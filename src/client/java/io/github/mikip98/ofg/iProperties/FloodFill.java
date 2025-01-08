package io.github.mikip98.ofg.iProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FloodFill {
    // ModId -> BlockstateId -> Set of Property value pairs
    Map<String, Map<String, Set<Map<String, Comparable>>>> alreadySupportedBlockstates;

    // Auto FloodFill format color -> all the blockstates w properties entries
    Map<Short, List<String>> floodFillColorEntries;


    @SuppressWarnings("rawtypes")
    public void generateFloodfillForLightEmittingBlocks(Map<String, Map<String, Map<Byte, Set<Map<String, Comparable>>>>> lightEmittingBlocksData) {

    }

    public void generateFloodfillForTranslucentBlocks(Map<String, List<String>> translucentBlocksData) {

    }
}
