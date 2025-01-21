package io.github.mikip98.opg.structures;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FloodFillSupport {
    // Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> has construction:
    // EntryId -> ModId -> blockstateId -> propertySet -> property -> value

    // Auto Floodfill entry data format
    public Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> lightEmittingSupport;
    public Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> translucentSupport;

    // By default (% is occlusion): 0% -> 50; 25% -> 51; 50% -> 52; 75% -> 53
    public Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> mainNonFullSupport;

    // Map<String, Map<String, Set<Map<String, Comparable<?>>>>> has construction:
    // ModId -> blockstateId -> propertySet -> property -> value

    // Specialized non-full floodfill support
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> carpetSupport;    // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> doorSupport;      // TODO: Implement
//    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> fenceSupport;     // TODO: Implement  // 25% ignore override?
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> slabSupport;      // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> stairSupport;     // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> trapdoorSupport;  // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> wallSupport;      // TODO: Implement



    public @NotNull LinkedHashMap<Short, String> getLightEmittingEntries() {
        return getStringEntriesFullSorted(lightEmittingSupport);
    }
    public @NotNull LinkedHashMap<Short, String> getTranslucentEntries() {
        return getStringEntriesFullSorted(translucentSupport);
    }
    public static @NotNull LinkedHashMap<Short, String> getStringEntriesFullSorted(Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> map) {
        LinkedHashMap<Short, String> result = new LinkedHashMap<>();
        getStringEntriesFull(map, result);
        return result;
    }


    public @NotNull Map<Short, String> getMainNonFullEntries() {
        return getStringEntriesFullSemiSorted(mainNonFullSupport);
    }
    public static @NotNull Map<Short, String> getStringEntriesFullSemiSorted(Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> map) {
        Map<Short, String> result = new HashMap<>();
        getStringEntriesFull(map, result);
        return result;
    }


    public static void getStringEntriesFull(Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> map, Map<Short, String> result) {
        List<Short> entryIds = new ArrayList<>(map.keySet());
        Collections.sort(entryIds);

        for (Short entryId : entryIds) {
            Map<String, Map<String, Set<Map<String, Comparable<?>>>>> mapMods = map.get(entryId);
            List<String> modIds = new ArrayList<>( mapMods.keySet());
            Collections.sort(modIds);

            for (String modId : modIds) {
                Map<String, Set<Map<String, Comparable<?>>>> mapBlockstates = mapMods.get(modId);
                List<String> blockstateIds = new ArrayList<>(mapBlockstates.keySet());
                Collections.sort(blockstateIds);

                for (String blockstateId : blockstateIds) {
                    Set<Map<String, Comparable<?>>> propertySets = mapBlockstates.get(blockstateId);
                    // Sort this so that smaller sets are first then with alphabetical order
                    // Sort the set of maps
                    propertySets.stream()
                            .sorted((m1, m2) -> {
                                int sizeCompare = Integer.compare(m1.size(), m2.size());
                                if (sizeCompare == 0) {
                                    // If sizes are equal, sort by keys
                                    List<String> keys1 = new ArrayList<>(m1.keySet());
                                    List<String> keys2 = new ArrayList<>(m2.keySet());
                                    Collections.sort(keys1);
                                    Collections.sort(keys2);
                                    return keys1.toString().compareTo(keys2.toString());
                                }
                                return sizeCompare;
                            })
                            .forEach(propertyMap -> {
                                // Iterate through the sorted map entries
                                propertyMap.entrySet().stream()
                                        .sorted(Map.Entry.comparingByKey())
                                        .forEach(entry -> {
                                            // TODO: finish
                                        });
                            });
                }
            }
        }
    }
}
