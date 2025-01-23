package io.github.mikip98.opg.generation;

import io.github.mikip98.opg.generation.floodfill.FloodFillGeneralGenerator;
import io.github.mikip98.opg.generation.floodfill.FloodFillSpecialGenerator;
import io.github.mikip98.opg.generation.sss.SSSGenerator;
import io.github.mikip98.opg.structures.DotPropertiesInfo;
import io.github.mikip98.opg.structures.FloodFillSupportIntermediate;
import io.github.mikip98.opg.structures.SSSSupport;

import java.util.*;

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
    FloodFillSupportIntermediate SpecialFloodFillSupport;
    SSSSupport sssSupport;


    public MainGenerator(DotPropertiesInfo dotPropertiesInfo) {
        this.controller = new Controller(dotPropertiesInfo);
        this.floodFillGeneralGenerator = new FloodFillGeneralGenerator(controller);
    }


    public void run() {
        pipeline.forEach(Runnable::run);
    }


    protected void generateFloodFillEmissiveEntries() {
        floodFillGeneralGenerator.generateFloodfillForLightEmittingBlocks();
    }

    protected void generateFloodFillTranslucentEntries() {
        floodFillGeneralGenerator.generateFloodfillForTranslucentBlocks();
    }

    protected void generateFloodFillMainIgnoreEntries() {
        floodFillGeneralGenerator.generateFloodfillForNonFullBlocks();
    }

    protected void generateFloodFillSpecialEntries() { SpecialFloodFillSupport = FloodFillSpecialGenerator.generateSpecialEntries(); }

    protected void generateSSS() {
        SSSSupport = SSSGenerator.generateSSS(controller);
    }
}
