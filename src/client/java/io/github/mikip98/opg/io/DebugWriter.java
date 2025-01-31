package io.github.mikip98.opg.io;

import io.github.mikip98.del.structures.SimplifiedProperty;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class DebugWriter {

    public static void saveNativelySupportedDataToFile(Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable<?>>>>> nativelySupportedBlockstates) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("oasis-property-generator").resolve("debug").resolve("natively-supported-blockstates.json");

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                LOGGER.error("Error while creating config directory", e);
            }
            try {
                Files.createFile(path);
            } catch (IOException e) {
                LOGGER.error("Error while creating natively-supported-blockstates.json", e);
            }
        }


        StringBuilder output = new StringBuilder();
        output.append("{\n");

        // ModId -> BlockstateId -> Set<Map<{property}, {value}>>
        for (Map.Entry<String, Map<String, Set<Map<SimplifiedProperty, Comparable<?>>>>> entry : nativelySupportedBlockstates.entrySet()) {
            String modId = entry.getKey();
            output.append("\t\"").append(modId).append("\": {\n");

            Map<String, Set<Map<SimplifiedProperty, Comparable<?>>>> blockstates = entry.getValue();
            for (Map.Entry<String, Set<Map<SimplifiedProperty, Comparable<?>>>> entry2 : blockstates.entrySet()) {
                String blockstateId = entry2.getKey();
                output.append("\t\t\"").append(blockstateId).append("\": [\n");

                Set<Map<SimplifiedProperty, Comparable<?>>> properties = entry2.getValue();
                if (properties == null) continue;
                for (Map<SimplifiedProperty, Comparable<?>> entry3 : entry2.getValue()) {
                    output.append("{\n");
                    for (Map.Entry<SimplifiedProperty, Comparable<?>> entry4 : entry3.entrySet()) {
                        output.append("\"").append(entry4.getKey()).append("\": ").append(entry4.getValue()).append(",\n");
                    }
                    output.deleteCharAt(output.length() - 1);  // Remove last comma
                    output.append("},\n");
                }
            }
            output.deleteCharAt(output.length() - 1);  // Remove last comma
            output.append("}\n");
        }

        output.deleteCharAt(output.length() - 1);  // Remove last comma
        output.append("}\n");


        try {
            Files.write(path, output.toString().getBytes());
        } catch (IOException e) {
            LOGGER.error("Error while writing natively-supported-blockstates.json", e);
        }
    }
}
