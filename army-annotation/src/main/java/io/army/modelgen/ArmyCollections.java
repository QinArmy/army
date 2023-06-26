package io.army.modelgen;

import java.util.ArrayList;
import java.util.HashMap;

abstract class ArmyCollections {

    private ArmyCollections() {
        throw new UnsupportedOperationException();
    }


    static <K, V> HashMap<K, V> hashMap() {
        return new FinalHashMap<>();
    }

    static <K, V> HashMap<K, V> hashMap(int initialCapacity) {
        return new FinalHashMap<>(initialCapacity);
    }

    static <E> ArrayList<E> arrayList() {
        return new FinalArrayList<>();
    }


    private static final class FinalHashMap<K, V> extends HashMap<K, V> {


        private FinalHashMap() {
        }

        private FinalHashMap(int initialCapacity) {
            super(initialCapacity);
        }


    }

    private static final class FinalArrayList<E> extends ArrayList<E> {

        private FinalArrayList() {
        }
    }


}
