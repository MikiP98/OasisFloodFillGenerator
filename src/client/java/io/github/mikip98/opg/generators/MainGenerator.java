package io.github.mikip98.opg.generators;

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
    public MainGenerator(Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates) {
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

    }

    protected void generateFloodFillIgnoreEntries() {

    }

    protected void generateSSS() {
        SSS.generateSSS(controller);
    }
}
