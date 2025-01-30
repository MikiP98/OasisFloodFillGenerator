package io.github.mikip98.opg.generation;

import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.objects.DotPropertiesInfo;

import java.util.*;

public class Controller {

    // ModId -> BlockstateId -> Set of Property value pairs
    // If 'Set<Map<SimplifiedProperty, Comparable>>' is 'null', all the blockstate's property combinations are already supported
    public Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable<?>>>>> alreadySupportedBlockstates;
    // ModId -> list of ItemIds
    public Map<String, Set<String>> alreadySupportedItems;


    public Map<String, Integer> newSupportStats = new HashMap<>();
    // ModId -> Number of supported blockstates


    public Controller(DotPropertiesInfo data) {
        this.alreadySupportedBlockstates = data.nativelySupportedBlockstates;
        this.alreadySupportedItems = data.nativelySupportedItems;
    }


    public List<String> getNotSupportedBlockstates(String modId, String blockstateId) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        List<String> unsupportedBlockstatesWProperties = new ArrayList<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            // If 'null', all the blockstates automation combinations are already supported
            if (alreadySupportedBlockstates.get(modId).get(blockstateId) != null) {
                // TODO: Add the missing logic
            } else {
                return unsupportedBlockstatesWProperties;
            }
        } else {
            unsupportedBlockstatesWProperties.add(blockstateId);
            newSupportStats.putIfAbsent(modId, 0);
            newSupportStats.put(modId, newSupportStats.get(modId) + 1);
        }

        alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, null);
        return unsupportedBlockstatesWProperties;
    }

    public List<String> getNotSupportedBlockstates(String modId, String blockstateId, Set<Map<SimplifiedProperty, Comparable<?>>> propertySets) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        List<String> unsupportedBlockstatesWProperties = new ArrayList<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            // If 'null', all the blockstates automation combinations are already supported
            if (alreadySupportedBlockstates.get(modId).get(blockstateId) != null) {
                for (Map<SimplifiedProperty, Comparable<?>> propertySet : propertySets) {
                    if (!alreadySupportedBlockstates.get(modId).get(blockstateId).contains(propertySet)) {
                        String[] properties = new String[propertySet.size()];
                        List<SimplifiedProperty> propertyKeys = new ArrayList<>(propertySet.keySet());
                        propertyKeys.sort(Comparator.comparing(p -> p.name));
                        for (int i = 0; i < propertyKeys.size(); i++) {
                            properties[i] = propertyKeys.get(i).name + "=" + propertySet.get(propertyKeys.get(i));
                        }

                        unsupportedBlockstatesWProperties.add(blockstateId + ":" + String.join(":", properties));
                        alreadySupportedBlockstates.get(modId).get(blockstateId).add(propertySet);
                    }
                }
            } else {
                return unsupportedBlockstatesWProperties;
            }
        } else {
            for (Map<SimplifiedProperty, Comparable<?>> propertySet : propertySets) {
                String[] properties = new String[propertySet.size()];
                List<SimplifiedProperty> propertyKeys = new ArrayList<>(propertySet.keySet());
                propertyKeys.sort(Comparator.comparing(p -> p.name));
                for (int i = 0; i < propertyKeys.size(); i++) {
                    properties[i] = propertyKeys.get(i).name + "=" + propertySet.get(propertyKeys.get(i));
                }

                unsupportedBlockstatesWProperties.add(blockstateId + ":" + String.join(":", properties));
            }
            alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, propertySets);
            newSupportStats.putIfAbsent(modId, 0);
            newSupportStats.put(modId, newSupportStats.get(modId) + 1);
        }

        return unsupportedBlockstatesWProperties;
    }
}
