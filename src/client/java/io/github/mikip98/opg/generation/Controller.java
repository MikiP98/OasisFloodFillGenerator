package io.github.mikip98.opg.generation;

import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.objects.DotPropertiesInfo;

import java.util.*;
import java.util.logging.Logger;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

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


    public Set<Map<String, Comparable<?>>> getNotSupportedBlockstates(String modId, String blockstateId) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        Set<Map<String, Comparable<?>>> unsupportedBlockstatesWProperties = new HashSet<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            Set<Map<SimplifiedProperty, Comparable<?>>> supportedStates = alreadySupportedBlockstates.get(modId).get(blockstateId);

            // If 'null', all the blockstates automation combinations are already supported
            if (supportedStates == null) return null;

            // TODO: Add the missing logic
            Set<Map<SimplifiedProperty, Comparable<?>>> missingCombinations = generateMissingCombinations(new HashMap<>(), supportedStates);
            if (missingCombinations.isEmpty()) return null;

            for (Map<SimplifiedProperty, Comparable<?>> missingSet : missingCombinations) {
                if (missingSet.isEmpty()) throw new RuntimeException("Empty propertySet map from " + modId + ":" + blockstateId);
                Map<String, Comparable<?>> formattedSet = new HashMap<>();
                for (Map.Entry<SimplifiedProperty, Comparable<?>> entry : missingSet.entrySet()) {
                    formattedSet.put(entry.getKey().name, entry.getValue());
                }
                unsupportedBlockstatesWProperties.add(formattedSet);
            }

//            LOGGER.info("Missing combinations: {}", missingCombinations);
            return unsupportedBlockstatesWProperties;
        } else {
            newSupportStats.putIfAbsent(modId, 0);
            newSupportStats.put(modId, newSupportStats.get(modId) + 1);
        }

        alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, null);
        return unsupportedBlockstatesWProperties;
    }

    /**
     * @return Set of possible blockstate property combinations, empty if no property combinations are needed OR null if the blockstate is already fully supported
     */
    public Set<Map<String, Comparable<?>>> getNotSupportedBlockstates(String modId, String blockstateId, Set<Map<SimplifiedProperty, Comparable<?>>> propertySets) {
        // {blockstateId}:{property1Name}={property2Value}:{property2Name}={property2Value}...
        Set<Map<String, Comparable<?>>> unsupportedBlockstatesWProperties = new HashSet<>();

        if (alreadySupportedBlockstates.containsKey(modId) && alreadySupportedBlockstates.get(modId).containsKey(blockstateId)) {
            Set<Map<SimplifiedProperty, Comparable<?>>> supportedStates = alreadySupportedBlockstates.get(modId).get(blockstateId);

            // If 'null', all the blockstates automation combinations are already supported
            if (supportedStates == null) return null;

            for (Map<SimplifiedProperty, Comparable<?>> newSet : propertySets) {
                if (newSet.isEmpty()) {
                    throw new RuntimeException("Empty propertySet map from " + modId + ":" + blockstateId);
                }

                // Find missing property combinations
                Set<Map<SimplifiedProperty, Comparable<?>>> missingCombinations = generateMissingCombinations(newSet, supportedStates);
                if (missingCombinations.isEmpty()) continue;

                for (Map<SimplifiedProperty, Comparable<?>> missingSet : missingCombinations) {
                    Map<String, Comparable<?>> formattedSet = new HashMap<>();
                    for (Map.Entry<SimplifiedProperty, Comparable<?>> entry : missingSet.entrySet()) {
                        formattedSet.put(entry.getKey().name, entry.getValue());
                    }
                    unsupportedBlockstatesWProperties.add(formattedSet);
                }
            }
            if (unsupportedBlockstatesWProperties.isEmpty()) return null;
        } else {
            for (Map<SimplifiedProperty, Comparable<?>> propertySet : propertySets) {
                if (propertySet.isEmpty()) throw new RuntimeException("Empty propertySet map from " + modId + ":" + blockstateId);

                Map<String, Comparable<?>> map = new HashMap<>();
                for (Map.Entry<SimplifiedProperty, Comparable<?>> entry : propertySet.entrySet()) {
                    map.put(entry.getKey().name, entry.getValue());
                }
                unsupportedBlockstatesWProperties.add(map);
            }
            alreadySupportedBlockstates.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, propertySets);
            newSupportStats.putIfAbsent(modId, 0);
            newSupportStats.put(modId, newSupportStats.get(modId) + 1);
        }

//        LOGGER.info("Missing '{}' combinations, for blockstate '{}': {}", unsupportedBlockstatesWProperties.size(), blockstateId, unsupportedBlockstatesWProperties);
        return unsupportedBlockstatesWProperties;
    }



    protected Set<Map<SimplifiedProperty, Comparable<?>>> generateMissingCombinations(Map<SimplifiedProperty, Comparable<?>> newSet, Set<Map<SimplifiedProperty, Comparable<?>>> supportedStates) {
        // Generate all possible combinations of property sets
        // Then remove the ones that are already supported or do not match the new set

        // Iterate through required properties from newSet and remove every set that does not contain them
        // + remove already supported sets that do not have required properties from the next step
        // Then iterate through every already supported set


        // ALT

        // Iterate through already supported sets
        // In each set check if the newSet's properties match, if not continue
        // Iterate though every property of the old set that is not in the new set
        // For each of thought properties, generate all possible combinations and add them to the missing combinations
        // Do the last step recursively backwards fro every next property, a.k.a. do every next iteration on the resulting missing combinations set of the last one



//        LOGGER.info("\nGenerating missing properties...");
//        LOGGER.info("New set: {}", newSet);
//        LOGGER.info("Supported Sets: {}", supportedStates);
        Set<Map<SimplifiedProperty, Comparable<?>>> missingCombinations = new HashSet<>();


        Set<Map<SimplifiedProperty, Comparable<?>>> relevantSupportedSets = new HashSet<>();
        Collection<SimplifiedProperty> newSetsProperties = newSet.keySet();

        // Iterate through already supported sets
        // In each set check if the newSet's properties match
        for (Map<SimplifiedProperty, Comparable<?>> supported : supportedStates) {
            boolean propertiesMatch = true;
            for (SimplifiedProperty property : newSetsProperties) {
//                LOGGER.info("Iteration");
//                LOGGER.info("Supported: {}", supported);
//                LOGGER.info("new: {}", newSet);
//                LOGGER.info("supported.containsKey(property) = {}", supported.containsKey(property));
                if (supported.containsKey(property)) {
//                if (containsKey(supported, property)) {
//                    LOGGER.info("supported: {}; new: {}", supported.get(property), newSet.get(property));
                    if (!supported.get(property).equals(newSet.get(property))) {
                        propertiesMatch = false;
                        break;
                    }
                }
            }
            if (propertiesMatch) relevantSupportedSets.add(supported);
        }
//        LOGGER.info("There are '{}' relevant property sets", relevantSupportedSets.size());
        if (relevantSupportedSets.isEmpty()) return new HashSet<>(Set.of(newSet));

        for (Map<SimplifiedProperty, Comparable<?>> supported : relevantSupportedSets) {
            if (supported.equals(newSet)) continue;

            Set<Map<SimplifiedProperty, Comparable<?>>> missingCombinationsOfTheSet = new HashSet<>();
            missingCombinationsOfTheSet.add(newSet);

            Collection<SimplifiedProperty> propertiesOfTheSet = supported.keySet();
            // Iterate though every property of the old set that is not in the new set
            for (SimplifiedProperty propertyOfTheSet : propertiesOfTheSet) {
                if (newSetsProperties.contains(propertyOfTheSet)) continue;

                Set<Map<SimplifiedProperty, Comparable<?>>> newMissingCombinationsOfTheSet = new HashSet<>();
                // Iterate through all possible values of the property
                for (Comparable<?> value : propertyOfTheSet.allowedValues) {
                    if (value.equals(supported.get(propertyOfTheSet))) continue;

                    for (Map<SimplifiedProperty, Comparable<?>> missingCombination : missingCombinationsOfTheSet) {
                        missingCombination.put(propertyOfTheSet, value);
                        newMissingCombinationsOfTheSet.add(missingCombination);
                    }
                }
                missingCombinationsOfTheSet = newMissingCombinationsOfTheSet;
            }

            for (Map<SimplifiedProperty, Comparable<?>> missingCombination : missingCombinationsOfTheSet) {
                if (!missingCombination.isEmpty()) missingCombinations.add(missingCombination);
            }
//            missingCombinations.addAll(missingCombinationsOfTheSet);
        }

//        LOGGER.info("Generated '{}' missing combinations", missingCombinations.size());
        return missingCombinations;
    }


//    private boolean containsKey(Map<SimplifiedProperty, Comparable<?>> supported, SimplifiedProperty property) {
//        for (SimplifiedProperty mapProperty : supported.keySet()) {
//            if (mapProperty.name.equals(property.name)) {
//                LOGGER.info("Found matching property name: {}", mapProperty.name);
//
//                LOGGER.info("mapProperty.allowedValues.equals(property.allowedValues) = {}", mapProperty.allowedValues.equals(property.allowedValues));
//                LOGGER.info("mapProperty.converter.equals(property.converter) = {}", mapProperty.converter.equals(property.converter));
//
//                LOGGER.info("mapProperty.equals(property) = {}", mapProperty.equals(property));
//                LOGGER.info("supported.containsKey(property) = {}", supported.containsKey(property));
//
//                return true;
//            }
//        }
//        return false;
//    }
}
