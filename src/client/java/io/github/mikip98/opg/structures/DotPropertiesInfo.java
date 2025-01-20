package io.github.mikip98.opg.structures;

import io.github.mikip98.del.api.OtherAPI;
import io.github.mikip98.del.structures.SimplifiedProperty;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class DotPropertiesInfo {
    @SuppressWarnings("rawtypes")
    public Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> nativelySupportedBlockstates;
    // ModId -> BlockstateId -> Set of Property value pairs
    public Map<Short, Map<String, List<String>>> existingAutoSupport;

    @SuppressWarnings("rawtypes")
    public DotPropertiesInfo(
            Map<String, Map<String, Set<Map<String, String>>>> nativelySupportedBlockstatesWPropertyStrings,
            Map<Short, Map<String, List<String>>> existingAutoSupport
    ) {
        this.existingAutoSupport = existingAutoSupport;

        Map<String, SimplifiedProperty> propertyName2SimplifiedPropertyMap = OtherAPI.getPropertyName2SimplifiedPropertyMap();

        this.nativelySupportedBlockstates = new HashMap<>();
        for (Map.Entry<String, Map<String, Set<Map<String, String>>>> modEntry : nativelySupportedBlockstatesWPropertyStrings.entrySet()) {
            String modId = modEntry.getKey();
            Map<String, Set<Map<String, String>>> blockstates = modEntry.getValue();

            for (Map.Entry<String, Set<Map<String, String>>> blockstateEntry : blockstates.entrySet()) {
                String blockstateId = blockstateEntry.getKey();
                Set<Map<String, String>> propertySets = blockstateEntry.getValue();

                Set<Map<SimplifiedProperty, Comparable>> simplifiedPropertySets = new HashSet<>();
                for (Map<String, String> propertySet : propertySets) {
                    Map<SimplifiedProperty, Comparable> simplifiedPropertySet = new HashMap<>();

                    for (Map.Entry<String, String> propertyEntry : propertySet.entrySet()) {
                        String propertyName = propertyEntry.getKey();
                        String propertyValue = propertyEntry.getValue();
                        if (propertyName2SimplifiedPropertyMap.containsKey(propertyName)) {
                            SimplifiedProperty simplifiedProperty = propertyName2SimplifiedPropertyMap.get(propertyName);
                            simplifiedPropertySet.put(simplifiedProperty, simplifiedProperty.converter.apply(propertyValue));
                        } else {
                            LOGGER.error("Unknown property: {}", propertyName);
                            simplifiedPropertySet.put(new SimplifiedProperty(propertyName, null, null), propertyValue);
                        }
                    }

                    simplifiedPropertySets.add(simplifiedPropertySet);
                }

                this.nativelySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, simplifiedPropertySets);
            }
        }
    }
}
