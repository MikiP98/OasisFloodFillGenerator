package io.github.mikip98.opg.generation;

import io.github.mikip98.opg.generation.floodfill.FloodFillGeneralGenerator;
import io.github.mikip98.opg.generation.floodfill.FloodFillSpecialGenerator;
import io.github.mikip98.opg.generation.sss.SSSGenerator;
import io.github.mikip98.opg.objects.DotPropertiesInfo;
import io.github.mikip98.opg.objects.FloodFillSupportIntermediate;
import io.github.mikip98.opg.objects.SSSSupportIntermediate;
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
    FloodFillSupportIntermediate floodFillSupport;
    SSSSupportIntermediate sssSupport;


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
