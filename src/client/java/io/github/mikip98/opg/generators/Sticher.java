package io.github.mikip98.opg.generators;

import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.opg.generators.floodfill.FloodFill;
import io.github.mikip98.opg.generators.sss.SSS;

import java.util.*;

public class Sticher {

    protected final List<Runnable> pipeline = List.of(
            this::generateFloodFillEmissiveEntries,
            this::generateFloodFillTranslucentEntries,
            this::generateSSS,
            this::generateFloodFillIgnoreEntries
    );


    @SuppressWarnings("rawtypes")
    protected final Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates;
    protected final FloodFill floodFill;
    protected final SSS sss;
    public Map<String, Long> newSupportStats = new HashMap<>();


    @SuppressWarnings("rawtypes")
    public Sticher(Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates) {
        this.alreadySupportedBlockstates = alreadySupportedBlockstates;
        this.floodFill = new FloodFill(alreadySupportedBlockstates, newSupportStats);
        this.sss = new SSS(alreadySupportedBlockstates, newSupportStats);
    }

    public void run() {
        pipeline.forEach(Runnable::run);
    }


    protected void generateFloodFillEmissiveEntries() {

    }

    protected void generateFloodFillTranslucentEntries() {

    }

    protected void generateFloodFillIgnoreEntries() {

    }

    protected void generateSSS() {

    }
}
