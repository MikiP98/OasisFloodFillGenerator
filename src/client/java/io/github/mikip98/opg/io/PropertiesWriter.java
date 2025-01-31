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

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

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
                String fileName = file.getFileName().toString();
                LOGGER.info("checking file: {}", fileName);
                if (fileName.endsWith(".properties")) {
                    LOGGER.info("Writing properties file: {}", fileName);
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
            boolean lineContinuation = false;
            boolean lineContinuationEnd = false;
            while ((line = br.readLine()) != null) {
                line = line.strip().toLowerCase();

                // TODO
                if (line.startsWith("block.")) {
                    String[] keyValue = line.split("=", 2);
                    Short entryId = Short.valueOf(keyValue[0].strip().substring(6));  // TODO: int?

                    if (Config.entriesOfInterest.contains(entryId)) {
                        // TODO
                    }

                    String[] entryEntries = keyValue[1].strip().split(" ");
                    String lastEntry = entryEntries[entryEntries.length - 1];

                    if (lastEntry.endsWith("/")) {
                        lineContinuation = true;
                    }
                    // TODO
                }
                new_properties.append(line).append("\n");
            }

            new_properties.append("\n\n\n# Auto FloodFill generated by 'Oasis Property Generator'\n");
            new_properties.append("#ifdef AUTO_GENERATED_FLOODFILL\n");

            new_properties.append("\n# Emissive\n");
            LinkedHashMap<Short, String> emissiveEntriesMap = floodFillSupport.getLightEmittingEntries();
            LOGGER.info("Writing '{}', emissive entries", emissiveEntriesMap.size());
            for (Short entryId : emissiveEntriesMap.keySet()) {
                new_properties
                        .append("block.").append(entryId)
                        .append(" = ")
                        .append(emissiveEntriesMap.get(entryId))
                        .append("\n");
            }

            new_properties.append("\n# Translucent\n");
            LinkedHashMap<Short, String> floodFillTranslucentEntriesMap = floodFillSupport.getTranslucentEntries();
            LOGGER.info("Writing '{}', translucent entries", floodFillTranslucentEntriesMap.size());
            for (Short entryId : floodFillTranslucentEntriesMap.keySet()) {
                new_properties
                        .append("block.").append(entryId)
                        .append(" = ")
                        .append(floodFillTranslucentEntriesMap.get(entryId))
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
