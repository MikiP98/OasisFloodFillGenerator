package io.github.mikip98.opg.generation;

import io.github.mikip98.del.api.CacheAPI;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.generation.floodfill.FloodFillGenerator;
import io.github.mikip98.opg.generation.sss.SSSGenerator;

import java.util.*;

public class MainGenerator {

    protected final List<Runnable> pipeline = List.of(
            this::generateFloodFillEmissiveEntries,
            this::generateFloodFillTranslucentEntries,
            // this::generateFloodFillSpecialEntries,
            this::generateSSS,
            this::generateFloodFillIgnoreEntries  // Main entries
    );

    protected final Controller controller;
    protected final FloodFillGenerator floodFillGenerator;


    @SuppressWarnings("rawtypes")
    public MainGenerator() {
        CacheAPI.cachePathsIfNotCached();
        Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates = null;

        this.controller = new Controller(alreadySupportedBlockstates);
        this.floodFillGenerator = new FloodFillGenerator(controller);
    }


    public void run() {
        pipeline.forEach(Runnable::run);
    }


    protected void generateFloodFillEmissiveEntries() {
        floodFillGenerator.generateFloodfillForLightEmittingBlocks();
    }

    protected void generateFloodFillTranslucentEntries() {
        floodFillGenerator.generateFloodfillForTranslucentBlocks();
    }

    protected void generateFloodFillIgnoreEntries() {
        floodFillGenerator.generateFloodfillForNonFullBlocks();
    }

    protected void generateSSS() {
        SSSGenerator.generateSSS(controller);
    }
}
