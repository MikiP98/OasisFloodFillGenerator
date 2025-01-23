package io.github.mikip98.opg.structures;

import java.util.Map;
import java.util.Set;

public class FloodFillSpecialSupport {
    // Support for blocks with dedicated categories

    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> carpetSupport;    // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> doorSupport;      // TODO: Implement
    //    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> fenceSupport;     // TODO: Implement  // 25% ignore override?
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> slabSupport;      // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> stairSupport;     // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> trapdoorSupport;  // TODO: Implement
    public Map<String, Map<String, Set<Map<String, Comparable<?>>>>> wallSupport;      // TODO: Implement
}
