package io.github.mikip98.opg.generation;

import io.github.mikip98.opg.generation.floodfill.FloodFillGeneralGenerator;
import io.github.mikip98.opg.generation.floodfill.FloodFillSpecialGenerator;
import io.github.mikip98.opg.generation.sss.SSSGenerator;
import io.github.mikip98.opg.objects.DotPropertiesInfo;
import io.github.mikip98.opg.generation.floodfill.FloodFillSupportIntermediate;
import io.github.mikip98.opg.generation.sss.SSSSupportIntermediate;
import io.github.mikip98.opg.structures.AutoSupport;

import java.util.*;

import static io.github.mikip98.opg.OasisPropertyGeneratorClient.LOGGER;

public class MainGenerator {

    protected final List<Runnable> pipeline = List.of(
            this::generateFloodFillEmissiveEntries,
            this::generateFloodFillTranslucentEntries,
            this::generateFloodFillSpecialEntries,
            this::generateSSS,
            this::generateFloodFillMainIgnoreEntries  // Main entries
    );

    protected final Controller controller;
    protected final FloodFillGeneralGenerator floodFillGeneralGenerator;
    FloodFillSupportIntermediate floodFillSupport = new FloodFillSupportIntermediate();
    SSSSupportIntermediate sssSupport = new SSSSupportIntermediate();


    public MainGenerator(DotPropertiesInfo dotPropertiesInfo) {
        this.controller = new Controller(dotPropertiesInfo);
        this.floodFillGeneralGenerator = new FloodFillGeneralGenerator(controller, floodFillSupport.generalFloodFillSupport);
    }

    public AutoSupport getSupport() {
        return new AutoSupport(floodFillSupport, sssSupport);
    }


    public void run() {
        pipeline.forEach(Runnable::run);
    }


    protected void generateFloodFillEmissiveEntries() {
        LOGGER.info("Generating flood fill for light emitting blocks");
        floodFillGeneralGenerator.generateFloodfillForLightEmittingBlocks();

        int blockCount = 0;
        for (Map.Entry<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> entry : floodFillSupport.generalFloodFillSupport.lightEmittingBlockSupport.entrySet()) {
            for (Map.Entry<String, Map<String, Set<Map<String, Comparable<?>>>>> modEntry : entry.getValue().entrySet()) {
                for (Map.Entry<String, Set<Map<String, Comparable<?>>>> blockstateEntry : modEntry.getValue().entrySet()) {
                    if (blockstateEntry.getValue() != null) blockCount += blockstateEntry.getValue().size();
                }
            }
        }
        LOGGER.info("Generated {} flood fill entries for light emitting blocks", blockCount);

        blockCount = 0;
        for (Map.Entry<Short, Map<String, Map<String, Set<Map<String, Comparable<?>>>>>> entry : floodFillGeneralGenerator.floodFillGeneralSupport.lightEmittingBlockSupport.entrySet()) {
            for (Map.Entry<String, Map<String, Set<Map<String, Comparable<?>>>>> modEntry : entry.getValue().entrySet()) {
                for (Map.Entry<String, Set<Map<String, Comparable<?>>>> blockstateEntry : modEntry.getValue().entrySet()) {
                    if (blockstateEntry.getValue() != null) blockCount += blockstateEntry.getValue().size();
                }
            }
        }
        LOGGER.info("Generated {} flood fill entries for light emitting blocks", blockCount);
    }

    protected void generateFloodFillTranslucentEntries() {
        LOGGER.info("Generating flood fill for translucent blocks");
        floodFillGeneralGenerator.generateFloodfillForTranslucentBlocks();
    }

    protected void generateFloodFillMainIgnoreEntries() {
        LOGGER.info("Generating flood fill for main non full blocks");
        floodFillGeneralGenerator.generateFloodfillForNonFullBlocks();
    }

    protected void generateFloodFillSpecialEntries() {
        LOGGER.info("Generating flood fill for special non full blocks");
        floodFillSupport.specialFloodFillSupport = FloodFillSpecialGenerator.generateSpecialEntries();
    }

    protected void generateSSS() {
        LOGGER.info("Generating SSS");
        sssSupport = SSSGenerator.generateSSS(controller);
    }
}
