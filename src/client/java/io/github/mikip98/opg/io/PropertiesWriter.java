package io.github.mikip98.opg.io;

import io.github.mikip98.opg.config.Config;
import io.github.mikip98.opg.objects.FloodFillSupportIntermediate;
import io.github.mikip98.opg.objects.SSSSupportIntermediate;
import io.github.mikip98.opg.structures.AutoSupport;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static io.github.mikip98.del.DataExtractionLibraryClient.LOGGER;

public class PropertiesWriter {
    protected FloodFillSupportIntermediate floodFillSupport;
    protected SSSSupportIntermediate sssSupport;

    public PropertiesWriter(AutoSupport autoSupport) {
        this.floodFillSupport = autoSupport.floodFillSupportIntermediate;
        this.sssSupport = autoSupport.sssSupportIntermediate;
    }

    public void writeToProperties() {
        Path configPath = FabricLoader.getInstance().getGameDir().resolve("config/oasis-property-generator");

        // Create the config directory if it doesn't exist
        try {
            Files.createDirectories(configPath);
        } catch (IOException e) {
            LOGGER.error("Error while creating config directory", e);
        }

        Set<String> ignoreFloodfillIds = new HashSet<>();
        for (int i = 0; i < Config.floodFillIgnoreEntryCount; i++) {
            ignoreFloodfillIds.add(String.valueOf(Config.floodFillIgnoreFirstEntryId + i));
        }
        Set<Character> numbers = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

        try (Stream<Path> paths = Files.list(configPath)) {
            for (Path file : paths.toList()) {
                String fileName = file.toString();
                if (fileName.endsWith(".properties")) {
                    if (fileName.startsWith("block.")) {
                        // Handle 'block.properties' type file
                        handleBlockPropertiesFile(file);
                    }
                    else if (fileName.startsWith("item.")) {
                        // Handle 'item.properties' type file
                        handleItemPropertiesFile(file);
                    }
                    else if (fileName.startsWith("entity.")) {
                        // Handle 'entity.properties' type file
                        // TODO?
                        continue;
                    }
                    else if (fileName.startsWith("empty")) {
                        // Handle 'empty.properties' type file
                        handleEmptyPropertiesFile(file);
                    }
                }

                // Write the properties file
                StringBuilder new_properties = new StringBuilder();

                try(BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {
                    String line;
                    short entryId;
                    short lastEntryId = -1;

//                    short entryId = Config.floodFillIgnoreFirstEntryId;
//                    boolean isEntryFloodfillIgnore = false;
                    while ((line = br.readLine()) != null) {
                        line = line.strip().toLowerCase();

                        if (line.startsWith("block.")) {

                        }

//                        if (
//                                line.strip().length() > 6 + String.valueOf(Config.floodFillIgnoreFirstEntryId).length()
//                                && ignoreFloodfillIds.contains(line.strip().substring(6, 6 + String.valueOf(Config.floodFillIgnoreFirstEntryId).length()))
//                                && !numbers.contains(line.strip().charAt(6 + String.valueOf(Config.floodFillIgnoreFirstEntryId).length()))
//                        ) isEntryFloodfillIgnore = true;
//
//                        new_properties.append(line);
//
//                        if (isEntryFloodfillIgnore && !line.strip().endsWith("\\")) {
//                            new_properties.append(" \\\n");
//                            new_properties.append(" #ifdef AUTO_GENERATED_FLOODFILL\n");
//
////                            new_properties.append(' ').append(prepareMessage(floodFillIgnoreEntries.get(entryId)));
//
//                            new_properties.append("\n #endif");
//                            entryId++;
//                            isEntryFloodfillIgnore = false;
//                        }
//
//                        new_properties.append("\n");
                    }
                } catch (IOException e) {
                    LOGGER.error("Error while reading properties file", e);
                }

                try {
                    Files.write(file, new_properties.toString().getBytes());
                } catch (IOException e) {
                    LOGGER.error("Error while writing properties file", e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while writing properties file", e);
        }
    }

    // Blocks
    protected void handleBlockPropertiesFile(Path file) {
        StringBuilder new_properties = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.strip().toLowerCase();

                if (line.startsWith("block.")) {

                }
                // TODO
            }

            new_properties.append("\n\n\n# Auto FloodFill generated by 'Oasis Property Generator'\n");
            new_properties.append("#ifdef AUTO_GENERATED_FLOODFILL\n");

            new_properties.append("\n# Emissive\n");
            Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> emissiveSupport = floodFillSupport.generalFloodFillSupport.lightEmittingSupport;
            for (Short entryId : emissiveSupport.keySet()) {
                new_properties
                        .append("block.").append(entryId)
                        .append(" = ")
                        .append(emissiveSupport.get(entryId))
                        .append("\n");
            }

            new_properties.append("\n# Translucent\n");
            Map<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> floodFillTranslucentEntries = floodFillSupport.generalFloodFillSupport.translucentSupport;
            for (Short entryId : floodFillTranslucentEntries.keySet()) {
                new_properties
                        .append("block.").append(entryId)
                        .append(" = ")
                        .append(floodFillTranslucentEntries.get(entryId))
                        .append("\n");
            }

            new_properties.append("\n#endif\n");

//            new_properties.append("\n# Ignored by Floodfill\n");
//            entryIds = new ArrayList<>(floodFillIgnoreEntries.keySet());
//            Collections.sort(entryIds);
//            for (Short entryId : entryIds) {
//                new_properties
//                        .append("block.").append(entryId)
//                        .append(" = ")
//                        .append(prepareMessage(floodFillIgnoreEntries.get(entryId)))
//                        .append("\n");
//            }

            writeToFile(file, new_properties);
        } catch (IOException e) {
            LOGGER.error("Error while writing properties file", e);
        }
    }

    // Items
    protected static void handleItemPropertiesFile(Path file) {
        StringBuilder new_properties = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.strip().toLowerCase();

                if (line.startsWith("item.")) {

                }
                // TODO
            }

            writeToFile(file, new_properties);
        } catch (IOException e) {
            LOGGER.error("Error while writing properties file", e);
        }
    }

    // Empty
    protected static void handleEmptyPropertiesFile(Path file) {
        StringBuilder new_properties = new StringBuilder();

        // TODO

        writeToFile(file, new_properties);
    }

    protected static void writeToFile(Path file, StringBuilder new_properties) {
        try {
            Files.write(file, new_properties.toString().getBytes());
        } catch (IOException e) {
            LOGGER.error("Error while writing properties file", e);
        }
    }
}
