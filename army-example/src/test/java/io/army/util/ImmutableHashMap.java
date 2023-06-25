package io.army.util;

import io.army.type.ImmutableSpec;

import java.util.HashMap;
import java.util.Map;

public final class ImmutableHashMap<K, V> extends HashMap<K, V> implements ImmutableSpec {

    public static <K, V> ImmutableHashMap<K, V> hashMap() {
        return new ImmutableHashMap<>();
    }

    public static <K, V> ImmutableHashMap<K, V> hashMap(int initialCapacity) {
        return new ImmutableHashMap<>(initialCapacity);
    }


    public static <K, V> ImmutableHashMap<K, V> hashMap(Map<? extends K, ? extends V> map) {
        return new ImmutableHashMap<>(map);
    }


    private ImmutableHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    private ImmutableHashMap() {
    }

    private ImmutableHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }


}
