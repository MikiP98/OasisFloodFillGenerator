package io.github.mikip98.opg.objects;

import io.github.mikip98.del.api.OtherAPI;
import io.github.mikip98.del.structures.EProperty;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.structures.AutoSupport;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class DotPropertiesInfo {
    // ModId -> BlockstateId -> Set of Property value pairs
    // If the set is null, all the blockstates automation combinations are already supported
    public Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable<?>>>>> nativelySupportedBlockstates;
    // ModId -> list of ItemIds
    public Map<String, Set<String>> nativelySupportedItems;

    public AutoSupport existingAutoSupport;

    public DotPropertiesInfo(
            Map<String, Map<String, Set<Map<String, String>>>> nativelySupportedBlockstatesWPropertyStrings,
            Map<String, Set<String>> nativelySupportedItems,
            AutoSupport existingAutoSupport
    ) {
        this.nativelySupportedItems = nativelySupportedItems;
        this.existingAutoSupport = existingAutoSupport;

        Map<String, EProperty> propertyName2SimplifiedPropertyMap = OtherAPI.getPropertyName2EPropertyMap();

        this.nativelySupportedBlockstates = new HashMap<>();
        for (Map.Entry<String, Map<String, Set<Map<String, String>>>> modEntry : nativelySupportedBlockstatesWPropertyStrings.entrySet()) {
            String modId = modEntry.getKey();
            Map<String, Set<Map<String, String>>> blockstates = modEntry.getValue();

            for (Map.Entry<String, Set<Map<String, String>>> blockstateEntry : blockstates.entrySet()) {
                String blockstateId = blockstateEntry.getKey();

                Set<Map<String, String>> propertySets = blockstateEntry.getValue();
                Set<Map<SimplifiedProperty, Comparable<?>>> simplifiedPropertySets;

                if (propertySets != null) {
                    simplifiedPropertySets = new HashSet<>();

                    for (Map<String, String> propertySet : propertySets) {
                        Map<SimplifiedProperty, Comparable<?>> simplifiedPropertySet = new HashMap<>();

                        for (Map.Entry<String, String> propertyEntry : propertySet.entrySet()) {
                            String propertyName = propertyEntry.getKey();
                            String propertyValue = propertyEntry.getValue();
                            if (propertyName2SimplifiedPropertyMap.containsKey(propertyName)) {
                                if (propertyName2SimplifiedPropertyMap.get(propertyName) instanceof SimplifiedProperty simplifiedProperty) {
//                                    LOGGER.info("Property: {} = {}", propertyName, propertyValue);
//                                    LOGGER.info("Allowed values: {}", simplifiedProperty.allowedValues);
                                    simplifiedPropertySet.put(simplifiedProperty, simplifiedProperty.converter.apply(propertyValue));
                                } else {
                                    LOGGER.error("Quantum properties are not supported right now: {}", propertyName);
                                    simplifiedPropertySets = null;
                                    break;
                                }
                            } else {
                                LOGGER.error("Unknown property: {}", propertyName);
//                                simplifiedPropertySet.put(new SimplifiedProperty(propertyName, null, null), propertyValue);
                                simplifiedPropertySets = null;
                                break;
                            }
                        }

                        if (simplifiedPropertySets == null) break;
                        simplifiedPropertySets.add(simplifiedPropertySet);
                    }
                }
                else {
                    simplifiedPropertySets = null;
                }

                this.nativelySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, simplifiedPropertySets);
            }
        }
    }
}
