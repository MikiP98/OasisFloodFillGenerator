package io.github.mikip98.opg.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSSSupportIntermediate {
    public Map<Short, Map<String, List<String>>> simpleSSSSupport = new HashMap<>();

    public SSSSupportIntermediate(
            Map<Short, Map<String, List<String>>> simpleSSSSupport
    ) {
        this.simpleSSSSupport = simpleSSSSupport;
    }

    public Map<Short, String> getSSSSupportStringEntries() {
        Map<Short, String> result = new HashMap<>();

        for (Map.Entry<Short, Map<String, List<String>>> entry : simpleSSSSupport.entrySet()) {
            Short entryId = entry.getKey();
            Map<String, List<String>> mapMods = entry.getValue();
            String modIds = mapMods.keySet().toString().replaceAll("\\[|\\]", "").replace(",", "|");
            result.put(entryId, modIds);
        }

        return result;
    }
}
