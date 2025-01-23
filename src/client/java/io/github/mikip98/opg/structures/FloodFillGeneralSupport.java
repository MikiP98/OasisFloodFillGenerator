package io.github.mikip98.opg.structures;

import java.util.Map;
import java.util.Set;

public class FloodFillGeneralSupport {
    // General floodfill support

    // Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> has construction:
    // EntryId -> ModId -> blockstateId -> propertySet -> property -> value

    // Auto Floodfill entry data format
    public Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> lightEmittingSupport;
    public Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> translucentSupport;

    // By default (% is occlusion): 0% -> 50; 25% -> 51; 50% -> 52; 75% -> 53
    public Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> mainNonFullSupport;

    // Map<String, Map<String, Set<Map<String, Comparable<?>>>>> has construction:
    // ModId -> blockstateId -> propertySet -> property -> value
}
