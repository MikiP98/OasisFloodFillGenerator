package io.github.mikip98.opg.structures;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FloodFillSpecialSupport {
    // Support for blocks with dedicated categories

    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> carpetSupport;    // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> doorSupport;      // TODO: Implement
    //    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> fenceSupport;     // TODO: Implement  // 25% ignore override?
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> slabSupport;      // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> stairSupport;     // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> trapdoorSupport;  // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> wallSupport;      // TODO: Implement

    public FloodFillSpecialSupport() {
        this.carpetSupport = new LinkedHashMap<>();
        this.doorSupport = new LinkedHashMap<>();
//        this.fenceSupport = new LinkedHashMap<>();
        this.slabSupport = new LinkedHashMap<>();
        this.stairSupport = new LinkedHashMap<>();
        this.trapdoorSupport = new LinkedHashMap<>();
        this.wallSupport = new LinkedHashMap<>();
    }

    public LinkedHashMap<Short, String> getSpecialStringEntries() {
        LinkedHashMap<Short, String> result = new LinkedHashMap<>();

        // Carpets
        for (Map.Entry<String, Map<String, Set<Map<String, Comparable<?>>>>> entry : carpetSupport.entrySet()) {
            String modId = entry.getKey();
            Map<String, Set<Map<String, Comparable<?>>>> blockstates = entry.getValue();
            for (Map.Entry<String, Set<Map<String, Comparable<?>>>> blockstateEntry : blockstates.entrySet()) {
                String blockstateId = blockstateEntry.getKey();
                Set<Map<String, Comparable<?>>> properties = blockstateEntry.getValue();
                result.put((short) 0, modId + ":" + blockstateId + ":" + properties); // TODO: Replace the '0'
            }
        }

        // Doors

        // Fences

        // Slabs

        // Stairs

        // Trapdoors

        // Walls

        return result;
    }
}
