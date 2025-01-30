package io.github.mikip98.opg.structures;

import io.github.mikip98.opg.objects.FloodFillSupportIntermediate;
import io.github.mikip98.opg.objects.SSSSupportIntermediate;

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
