package io.github.mikip98.ofg.property;

import java.util.Map;
import java.util.Set;

public class propertyReader {

    @SuppressWarnings("rawtypes")
    public static Map<String, Map<String, Set<Map<String, Comparable>>>> getAlreadySupportedBlockstates() {
        return getAlreadySupportedBlockstates(true);
    }
    @SuppressWarnings("rawtypes")
    public static Map<String, Map<String, Set<Map<String, Comparable>>>> getAlreadySupportedBlockstates(boolean ignoreAutoFloodFillEntries) {
        return null;
    }
}
