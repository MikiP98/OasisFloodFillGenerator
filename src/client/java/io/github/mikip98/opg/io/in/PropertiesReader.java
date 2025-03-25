package io.github.mikip98.opg.io.in;

import io.github.mikip98.opg.objects.DotPropertiesInfo;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class PropertiesReader {

    public static @NotNull DotPropertiesInfo getDotPropertiesInfo() {
        Path configPath = FabricLoader.getInstance().getGameDir().resolve("config/oasis-property-generator");

        // Create the config directory if it doesn't exist
        try {
            Files.createDirectories(configPath);
        } catch (IOException e) {
            LOGGER.error("Error while creating config directory", e);
        }

        return new DotPropertiesInfo(
                getAlreadySupportedBlockstates(configPath),
                getAlreadySupportedItems(configPath),
                null
        );
    }

    protected static Map<String, Map<String, Set<Map<String, String>>>> getAlreadySupportedBlockstates(Path configPath) {
        Map<String, Map<String, Set<Map<String, String>>>> nativelySupportedBlockstates = new HashMap<>();

        Path blockPropertiesPath = configPath.resolve("block.properties");
        if (Files.exists(blockPropertiesPath)) {
            // Parse the properties file to get the already supported blockstates
            try (BufferedReader br = new BufferedReader(new FileReader(blockPropertiesPath.toString()))) {
                HashMap<String, String> defines = new HashMap<>();
                ArrayList<String> entries = new ArrayList<>();

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.strip().toLowerCase();
                    LOGGER.info("Line: {}", line);

                    // Check for already existing auto support
                    if (line.equalsIgnoreCase("#########                       AUTO GENERATED SHADER SUPPORT BY 'OASIS AUTO GENERATOR'                        #########")) {
                        do {
                            line = line.strip().toLowerCase();
                            if (line.startsWith("#define")) {
                                LOGGER.info("Found OAE define: {}", line);
                                // TODO
                            }
                        } while ((line = br.readLine()) != null && line.strip().startsWith("#"));
                        if (line == null) continue;
                        line = line.strip().toLowerCase();
                    }

                    // Check for defines in to later parse them into normal entries
                    if (line.startsWith("#define")) {
                        String[] parts = line.split(" ",3);
                        String defineName = parts[1];
                        String defineValue = parts[2];
                        defines.put(defineName, defineValue);
                        continue;
                    }

                    // Get already existing blockstates
                    if (line.startsWith("block.")) {
                        String[] parts = line.split("=", 2);

                        for (String entry : parts[1].split(" ")) {
                            if (defines.containsKey(entry)) {
                                entries.addAll(List.of(defines.get(entry).split(" ")));
                            }
                            else if (!entry.equals("\\")) entries.add(entry);
                        }
                        String lastEntry = entries.get(entries.size() - 1);

                        while (lastEntry.equals("\\")) {
                            line = br.readLine();
                            if (line == null) break;
                            line = line.strip().toLowerCase();

                            for (String entry : line.split(" ")) {
                                if (defines.containsKey(entry)) {
                                    entries.addAll(List.of(defines.get(entry).split(" ")));
                                }
                                else if (!entry.equals("\\")) entries.add(entry);
                            }
                            lastEntry = entries.get(entries.size() - 1);
                        }
                    }
                }

                addBlockstatesToMap(nativelySupportedBlockstates, entries);

            } catch (IOException e) {
                LOGGER.error("Error while reading block.properties", e);
            }
        }

        return nativelySupportedBlockstates;
    }

    protected static Map<String, Set<String>> getAlreadySupportedItems(Path configPath) {
        Map<String, Set<String>> nativelySupportedItems = new HashMap<>();

        Path itemPropertiesPath = configPath.resolve("item.properties");
        if (Files.exists(itemPropertiesPath)) {
            // Parse the properties file to get the already supported blockstates
            try (BufferedReader br = new BufferedReader(new FileReader(itemPropertiesPath.toString()))) {
                String line;
                String[] itemIdsTable;
                LinkedList<String> itemIdsArray;

                while ((line = br.readLine()) != null) {
                    line = line.strip().toLowerCase();
                    LOGGER.info("Line: {}", line);

                    // TODO
                }
            } catch (IOException e) {
                LOGGER.error("Error while reading block.properties", e);
            }
        }

        return nativelySupportedItems;
    }


    /**
     * Adds to the map the blockstates with their property combinations, or blockstate with null if no properties are present
     */
    protected static void addBlockstatesToMap(Map<String, Map<String, Set<Map<String, String>>>> map, List<String> blockstatesData) {
        for (String blockstateData : blockstatesData) {
            String[] parts = blockstateData.split(":", 3); // 0 -> modId, 1 -> blockstateId, 2 -> properties

            String modId;
            String blockstateId;
            List<String> propertiesData;

            if (parts.length == 1) {
                // E.G. 'flower_pot' -> no modId (minecraft), no properties
                modId = "minecraft";
                blockstateId = parts[0];
                propertiesData = null;
            }
            else if (parts[1].contains("=")) {
                // E.G. 'snow:layers=2' -> no modId (minecraft)
                modId = "minecraft";
                blockstateId = parts[0];
                propertiesData = new ArrayList<>();
                propertiesData.add(parts[1]);
                if (parts.length == 3) propertiesData.addAll(List.of(parts[2].split(":")));
            }
            else if (parts.length == 2) {
                // E.G. 'minecraft:flowing_water' -> no properties
                modId = parts[0];
                blockstateId = parts[1];
                propertiesData = null;
            } else {
                // E.G. 'minecraft:snow:layers=2' -> full/everything
                modId = parts[0];
                blockstateId = parts[1];
                propertiesData = List.of(parts[2].split(":"));
            }

            if (propertiesData == null || propertiesData.isEmpty()) {
                map.computeIfAbsent(modId, k -> new HashMap<>()).put(blockstateId, null);
            } else {
                if (map.containsKey(modId) && map.get(modId).containsKey(blockstateId) && map.get(modId).get(blockstateId) == null) continue;

//                Set<Map<String, String>> propertySets = new HashSet<>();
                Map<String, String> currentPropertySet = new HashMap<>();
                for (String propertyData : propertiesData) {
                    String[] propertyParts = propertyData.split("=", 2);
                    String propertyName = propertyParts[0];
                    String propertyValue = propertyParts[1];
                    currentPropertySet.put(propertyName, propertyValue);
                }
                map.computeIfAbsent(modId, k -> new HashMap<>()).computeIfAbsent(blockstateId, k -> new HashSet<>()).add(currentPropertySet);
            }
        }
    }
}
