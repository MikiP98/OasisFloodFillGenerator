package io.github.mikip98.opg.io;

import io.github.mikip98.opg.structures.DotPropertiesInfo;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.github.mikip98.del.DataExtractionLibraryClient.LOGGER;

public class PropertiesReader {

    @SuppressWarnings("rawtypes")
    public static Map<String, Map<String, Set<Map<String, String>>>> getAlreadySupportedBlockstatesWIds() {
        return getAlreadySupportedBlockstatesWIds(true);
    }
    @SuppressWarnings("rawtypes")
    public static @NotNull DotPropertiesInfo getDotPropertiesInfo() {
        Path configPath = FabricLoader.getInstance().getGameDir().resolve("config/oasis-property-generator");

        // Create the config directory if it doesn't exist
        try {
            Files.createDirectories(configPath);
        } catch (IOException e) {
            LOGGER.error("Error while creating config directory", e);
        }

        Map<String, Map<String, Set<Map<String, String>>>> alreadySupportedBlockstatesWIds = new HashMap<>();
        Path blockPropertiesPath = configPath.resolve("block.properties");
        if (Files.exists(blockPropertiesPath)) {
            // Parse the properties file to get the already supported blockstates
            try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(blockPropertiesPath)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.strip();
                    if (line.startsWith("block.") || line.startsWith("item.")) {

                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error while reading block.properties", e);
            }
        }

//        return alreadySupportedBlockstatesWIds;
        return new DotPropertiesInfo(alreadySupportedBlockstatesWIds, e);
    }
}
