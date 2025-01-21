package io.github.mikip98.opg.generation;

import io.github.mikip98.del.structures.SimplifiedProperty;

import java.util.*;

public class Controller {

    @SuppressWarnings("rawtypes")
    protected Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates;
    // If 'Set<Map<SimplifiedProperty, Comparable>>' is 'null', all the blockstates automation combinations are already supported
    // ModId -> BlockstateId -> Set of Property value pairs

    public Map<String, Integer> newSupportStats = new HashMap<>();
    // ModId -> Number of supported blockstates


    @SuppressWarnings("rawtypes")
    public Controller(Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates) {
        this.alreadySupportedBlockstates = alreadySupportedBlockstates;
    }


    public List<String> getNotSupportedBlockstates(String modId, String blockstateId) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        List<String> unsupportedBlockstatesWProperties = new ArrayList<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            // If 'null', all the blockstates automation combinations are already supported
            if (alreadySupportedBlockstates.get(modId).get(blockstateId) != null) {
                // TODO: Add the missing logic
            }
        } else {
            unsupportedBlockstatesWProperties.add(blockstateId);
            newSupportStats.computeIfAbsent(modId, k -> 0);
            newSupportStats.put(modId, newSupportStats.get(modId) + 1);
        }

        alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, null);
        return unsupportedBlockstatesWProperties;
    }
}
