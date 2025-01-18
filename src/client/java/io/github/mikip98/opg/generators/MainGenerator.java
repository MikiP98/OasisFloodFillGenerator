package io.github.mikip98.opg.generators;

import io.github.mikip98.del.api.CacheAPI;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.generators.floodfill.FloodFill;
import io.github.mikip98.opg.generators.sss.SSS;

import java.util.*;

public class MainGenerator {

    protected final List<Runnable> pipeline = List.of(
            this::generateFloodFillEmissiveEntries,
            this::generateFloodFillTranslucentEntries,
            this::generateSSS,
            this::generateFloodFillIgnoreEntries
    );

    protected final Controller controller;
    protected final FloodFill floodFill;


    @SuppressWarnings("rawtypes")
    public MainGenerator() {
        CacheAPI.cachePathsIfNotCached();
        Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates = null;

        this.controller = new Controller(alreadySupportedBlockstates);
        this.floodFill = new FloodFill(controller);
    }


    public void run() {
        pipeline.forEach(Runnable::run);
    }


    protected void generateFloodFillEmissiveEntries() {
        floodFill.generateFloodfillForLightEmittingBlocks();
    }

    protected void generateFloodFillTranslucentEntries() {
        floodFill.generateFloodfillForTranslucentBlocks();
    }

    protected void generateFloodFillIgnoreEntries() {
        floodFill.generateFloodfillForNonFullBlocks();
    }

    protected void generateSSS() {
        SSS.generateSSS(controller);
    }
}
