package io.github.mikip98.opg.io;

import io.github.mikip98.opg.objects.DotPropertiesInfo;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static io.github.mikip98.del.DataExtractionLibraryClient.LOGGER;

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
                LinkedList<String> blockIdsArray;

                while ((line = br.readLine()) != null) {
                    line = line.strip().toLowerCase();

                    if (line_continuation) {
                        blockIdsTable = line.split(" ");
                        line_continuation = blockIdsTable[blockIdsTable.length - 1].equals("/");
                        blockIdsArray = new LinkedList<>(Arrays.stream(blockIdsTable).sorted().toList());
                    }
                    else if (line.startsWith("block.")) {
                        blockIdsTable = line.split("=", 2)[1].split(" ");
                        line_continuation = blockIdsTable[blockIdsTable.length - 1].equals("/");
                        blockIdsArray = new LinkedList<>(Arrays.stream(blockIdsTable).sorted().toList());
                    }
                    else if (line.startsWith("#define")) {
                        blockIdsTable = line.split(" ");
                        line_continuation = blockIdsTable[blockIdsTable.length - 1].equals("/");
                        blockIdsArray = new LinkedList<>(Arrays.stream(blockIdsTable).sorted().toList());  // .skip(2)
                        blockIdsArray.removeFirst(); blockIdsArray.removeFirst();
                        // '.skip(2)' removes '#define' and the define name, but is slow with .sorted()
                    } else continue;

                    if (line_continuation) blockIdsArray.removeLast();  // Remove the '/' fom the end
                    addBlockstates(nativelySupportedBlockstates, blockIdsArray);
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

                    if (line_continuation) {
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

                    if (line_continuation) itemIdsArray.removeLast();  // Remove the '/' fom the end
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

                Set<Map<String, String>> propertySets = new HashSet<>();
                for (String propertyData : propertiesData) {
                    String[] propertyParts = propertyData.split("=", 2);
                    String propertyName = propertyParts[0];
                    String propertyValue = propertyParts[1];
                    propertySets.add(Map.of(propertyName, propertyValue));
                }
                map.computeIfAbsent(modId, k -> new HashMap<>()).computeIfAbsent(blockstateId, k -> new HashSet<>()).addAll(propertySets);
            }
        }
    }
}
