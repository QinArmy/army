package io.army.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class RowMaps {

    private RowMaps() {
        throw new UnsupportedOperationException();
    }


    public static Map<String, Object> hashMap() {
        return _Collections.hashMap();
    }

    public static Map<String, Object> treeMap() {
        return new TreeMap<>();
    }


    public static Map<String, Object> linkedHashMap() {
        return new LinkedHashMap<>();
    }


}
