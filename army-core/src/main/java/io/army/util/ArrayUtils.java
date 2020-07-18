package io.army.util;

import io.army.lang.NonNull;
import io.army.lang.Nullable;

import java.util.*;

public abstract class ArrayUtils {


    @NonNull
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Set<T> asSet(@NonNull Collection<T> collection, @Nullable T... e) {
        Set<T> set = new HashSet<>(collection);
        if (e != null) {
            Collections.addAll(set, e);
        }
        return set;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asSet(@Nullable T... e) {
        return asSet(Collections.emptySet(), e);
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asUnmodifiableSet(@NonNull Collection<T> collection, @Nullable T... e) {
        return Collections.unmodifiableSet(asSet(collection, e));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asUnmodifiableSet(@Nullable T... e) {
        return asUnmodifiableSet(Collections.emptySet(), e);
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> asUnmodifiableList(@Nullable T... e) {
        List<T> list;
        if (e == null) {
            list = Collections.emptyList();
        } else {
            list = new ArrayList<>(e.length);
            Collections.addAll(list, e);
        }
        return Collections.unmodifiableList(list);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> asList(@NonNull Collection<T> collection, @Nullable T... addElements) {
        List<T> list;
        int size = collection.size();
        if (addElements != null) {
            size += addElements.length;
        }
        list = new ArrayList<>(size);
        list.addAll(collection);

        if (addElements != null) {
            Collections.addAll(list, addElements);
        }
        return list;
    }

    @NonNull
    public static <T> List<T> asList(@Nullable T... addElements) {
        if (addElements == null) {
            return new ArrayList<>(0);
        }
        List<T> list = new ArrayList<>(addElements.length);
        Collections.addAll(list, addElements);
        return list;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> asUnmodifiableList(Collection<T> collection, @Nullable T... addElements) {
        if (collection instanceof List
                && (addElements == null || addElements.length == 0)) {
            return Collections.unmodifiableList((List<T>) collection);
        }
        return Collections.unmodifiableList(asList(collection, addElements));
    }

    public static Map<Integer, Integer> asUnmodifiableMap(int[] array) {
        Map<Integer, Integer> map;
        switch (array.length) {
            case 0:
                map = Collections.emptyMap();
                break;
            case 1:
                map = Collections.singletonMap(0, array[0]);
                break;
            default:
                map = new HashMap<>((int) (array.length / 0.75f));
                for (int i = 0; i < array.length; i++) {
                    map.put(i, array[i]);
                }
                map = Collections.unmodifiableMap(map);
        }
        return map;
    }

    public static Map<Integer, Long> asUnmodifiableMap(long[] array) {
        Map<Integer, Long> map;
        switch (array.length) {
            case 0:
                map = Collections.emptyMap();
                break;
            case 1:
                map = Collections.singletonMap(0, array[0]);
                break;
            default:
                map = new HashMap<>((int) (array.length / 0.75f));
                for (int i = 0; i < array.length; i++) {
                    map.put(i, array[i]);
                }
                map = Collections.unmodifiableMap(map);
        }
        return map;
    }
}
