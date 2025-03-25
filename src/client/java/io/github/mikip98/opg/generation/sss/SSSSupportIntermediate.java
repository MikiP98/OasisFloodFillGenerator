package io.github.mikip98.opg.generation.sss;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class SSSSupportIntermediate {
    public Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> simpleSSSSupport;

    public SSSSupportIntermediate() {this.simpleSSSSupport = new HashMap<>();}
    public SSSSupportIntermediate(
            Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> simpleSSSSupport
    ) {
        this.simpleSSSSupport = simpleSSSSupport;
    }

    public LinkedHashMap<Short, String> getSSSSupportStringEntries() {
        LinkedHashMap<Short, String> result = new LinkedHashMap<>();

        List<Short> entryIds = new ArrayList<>(simpleSSSSupport.keySet());
        Collections.sort(entryIds);
        LOGGER.info("Converting '{}' SSS entries to strings: {}", entryIds.size(), simpleSSSSupport);

        for (short entryId : entryIds) {
            Map<String, Map<String, Set<Map<String, Comparable<?>>>>> mapMods = simpleSSSSupport.get(entryId);

            List<String> modIds = new ArrayList<>(mapMods.keySet());
            Collections.sort(modIds);

            List<String> stringEntry = new ArrayList<>(mapMods.keySet().size());

            for (String modId : modIds) {
                Map<String, Set<Map<String, Comparable<?>>>> mapBlockstates = mapMods.get(modId);
                List<String> blockstateIds = new ArrayList<>(mapBlockstates.keySet());
                Collections.sort(blockstateIds);

                List<String> stringMod = new ArrayList<>(mapBlockstates.keySet().size());

                for (String blockstateId : blockstateIds) {
                    Set<Map<String, Comparable<?>>> propertySets = mapBlockstates.get(blockstateId);
                    if (propertySets == null || propertySets.isEmpty()) {
                        stringMod.add(modId + ":" + blockstateId);
                        continue;
                    }

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
                                String[] properties = new String[propertyMap.size()];
                                AtomicInteger i = new AtomicInteger();

                                // Iterate through the sorted map entries
                                propertyMap.entrySet().stream()
                                        .sorted(Map.Entry.comparingByKey())
                                        .forEach(entry -> properties[i.getAndIncrement()] = entry.getKey() + "=" + entry.getValue());
                                if (properties.length == 0) throw new RuntimeException("No properties found for entryId " + entryId + " modId " + modId + " blockstateId " + blockstateId);   // result.put(entryId, modId + ":" + blockstateId);
                                else stringMod.add(modId + ":" + blockstateId + ":" + String.join(":", properties));
                            });
                }
                stringEntry.add(String.join(" ", stringMod));
            }
            result.put(entryId, String.join(" \\\n ", stringEntry));
        }

        return result;
    }
}
