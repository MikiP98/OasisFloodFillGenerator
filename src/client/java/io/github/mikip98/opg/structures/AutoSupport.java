package io.github.mikip98.opg.structures;

import io.github.mikip98.opg.generation.floodfill.FloodFillSupportIntermediate;
import io.github.mikip98.opg.generation.sss.SSSSupportIntermediate;

public class AutoSupport {
    // Floodfill (emission, translucency, ignore, walls, stairs, slabs)
    public FloodFillSupportIntermediate floodFillSupportIntermediate;

    /// SSS
    public SSSSupportIntermediate sssSupportIntermediate;


    public AutoSupport(
            FloodFillSupportIntermediate floodFillSupportIntermediate,
            SSSSupportIntermediate sssSupportIntermediate
    ) {
        this.floodFillSupportIntermediate = floodFillSupportIntermediate;
        this.sssSupportIntermediate = sssSupportIntermediate;
    }
}
