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

        // --- Blockstates ---
        Map<String, Map<String, Set<Map<String, String>>>> nativelySupportedBlockstates = new HashMap<>();

        Path blockPropertiesPath = configPath.resolve("block.properties");
        if (Files.exists(blockPropertiesPath)) {
            // Parse the properties file to get the already supported blockstates
            try (BufferedReader br = new BufferedReader(new FileReader(blockPropertiesPath.toString()))) {
                String line;
                boolean line_continuation = false;
                String[] blockIdsTable;
                List<String> blockIdsList;

                while ((line = br.readLine()) != null) {
                    line = line.strip().toLowerCase();

                    if (line_continuation) {
                        blockIdsTable = line.split(" ");
                    }
                    else if (line.startsWith("block.")) {
                        blockIdsTable = line.split("=", 2)[1].split(" ");
                    }
                    else if (line.startsWith("#define")) {
                        blockIdsTable = Arrays.stream(line.split(" ")).skip(2).toArray(String[]::new);
                    } else continue;

                    blockIdsList = new ArrayList<>(Arrays.stream(blockIdsTable).toList());
                    int lastElementIndex = blockIdsList.size() - 1;
                    line_continuation = blockIdsList.get(lastElementIndex).equals("\\");
                    if (line_continuation) blockIdsList.remove(lastElementIndex);  // Remove the '\' from the end

                    List<String> filteredBlockIds = new ArrayList<>();
                    for (String blockId : blockIdsList) {
                        if (blockId.isBlank() || blockId.equals("\\") || blockId.equals("\\n")) continue;
                        filteredBlockIds.add(blockId);
                    }

                    addBlockstates(nativelySupportedBlockstates, filteredBlockIds);
                }
            } catch (IOException e) {
                LOGGER.error("Error while reading block.properties", e);
            }
        }

        // --- Items ---
        Map<String, Set<String>> nativelySupportedItems = new HashMap<>();

        Path itemPropertiesPath = configPath.resolve("item.properties");
        if (Files.exists(itemPropertiesPath)) {
            // Parse the properties file to get the already supported blockstates
            try (BufferedReader br = new BufferedReader(new FileReader(itemPropertiesPath.toString()))) {
                String line;
                boolean line_continuation = false;
                String[] itemIdsTable;
                LinkedList<String> itemIdsArray;

                while ((line = br.readLine()) != null) {
                    line = line.strip().toLowerCase();

                    // TODO: Add defines support
                    if (line_continuation) {
                        if (line.startsWith("#ifdef")) continue;  // TODO: Make the auto support be skipped (added to stats instead)
                        itemIdsTable = line.split(" ");
                        line_continuation = itemIdsTable[itemIdsTable.length - 1].equals("/");
                        itemIdsArray = (LinkedList<String>) Arrays.stream(itemIdsTable).toList();
                    }
                    else if (line.startsWith("item.")) {
                        itemIdsTable = line.split("=", 2)[1].split(" ");
                        line_continuation = itemIdsTable[itemIdsTable.length - 1].equals("/");
                        itemIdsArray = (LinkedList<String>) Arrays.stream(itemIdsTable).toList();
                    }
                    else if (line.startsWith("#define")) {
                        itemIdsTable = line.split(" ");
                        line_continuation = itemIdsTable[itemIdsTable.length - 1].equals("/");
                        itemIdsArray = (LinkedList<String>) Arrays.stream(itemIdsTable).unordered().skip(2).toList();
                        // '.skip(2)' removes '#define' and the define name
                    } else continue;

                    LOGGER.info("Line: {}", line);
                    LOGGER.info("Last element: {}", itemIdsArray.getLast());
                    if (line_continuation) {
                        LOGGER.info("Line continuation detected, removing last element: {}", itemIdsArray.getLast());
                        itemIdsArray.removeLast();  // Remove the '/' fom the end
                        LOGGER.info("New last element: {}", itemIdsArray.getLast());
                    }

//                    itemIdsArray.stream().filter(s -> s.isBlank() || s.strip().equals("/")).forEach(itemIdsArray::remove);

                    for (String itemData : itemIdsArray) {
                        String[] parts = itemData.split(":");
                        String modId;
                        String itemId;
                        if (parts.length == 1) {
                            modId = "minecraft";
                            itemId = parts[0];
                        }
                        else if (parts.length == 2) {
                            modId = parts[0];
                            itemId = parts[1];
                        } else throw new RuntimeException("Invalid itemId property entry");
                        nativelySupportedItems.computeIfAbsent(modId, k -> new HashSet<>()).add(itemId);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error while reading block.properties", e);
            }
        }

//        return alreadySupportedBlockstatesWIds;
        return new DotPropertiesInfo(
                nativelySupportedBlockstates,
                nativelySupportedItems,
                null
        );
    }

    /**
     * Adds to the map the blockstates with their property combinations, or blockstate with null if no properties are present
     */
    protected static void addBlockstates(Map<String, Map<String, Set<Map<String, String>>>> map, List<String> blockstatesData) {
        for (String blockstateData : blockstatesData) {
            String[] parts = blockstateData.split(":", 3); // 0 -> modId, 1 -> blockstateId, 2 -> properties

            String modId;
            String blockstateId;
            List<String> propertiesData;

            if (parts.length == 1) {
                // E.G. 'flower_pot', no modId (minecraft), no properties
                modId = "minecraft";
                blockstateId = parts[0];
                propertiesData = null;
            }
            else if (parts[1].contains("=")) {
                // E.G. 'snow:layers=2', no modId (minecraft)
                modId = "minecraft";
                blockstateId = parts[0];
                propertiesData = new ArrayList<>();
                propertiesData.add(parts[1]);
                if (parts.length == 3) propertiesData.addAll(List.of(parts[2].split(":")));
            }
            else if (parts.length == 2) {
                // No properties, e.g. 'minecraft:flowing_water'
                modId = parts[0];
                blockstateId = parts[1];
                propertiesData = null;
            } else {
                // E.G. 'minecraft:snow:layers=2', full
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
