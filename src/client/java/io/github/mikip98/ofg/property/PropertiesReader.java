package io.github.mikip98.ofg.property;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.github.mikip98.del.DataExtractionLibraryClient.LOGGER;

public class PropertiesReader {

    @SuppressWarnings("rawtypes")
    public static Map<String, Map<String, Set<Map<String, Comparable>>>> getAlreadySupportedBlockstatesWIds() {
        return getAlreadySupportedBlockstatesWIds(true);
    }
    @SuppressWarnings("rawtypes")
    public static @NotNull Map<String, Map<String, Set<Map<String, Comparable>>>> getAlreadySupportedBlockstatesWIds(boolean ignoreAutoFloodFillEntries) {
        Path configPath = FabricLoader.getInstance().getGameDir().resolve("config/oasis-floodfill-generator");

        // Create the config directory if it doesn't exist
        try {
            Files.createDirectories(configPath);
        } catch (IOException e) {
            LOGGER.error("Error while creating config directory", e);
        }

        Path blockPropertiesPath = configPath.resolve("block.properties");

        if (Files.exists(blockPropertiesPath)) {
            // Parse the properties file to get the already supported blockstates
        }

        return new HashMap<>();
    }
}
