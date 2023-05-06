package io.army.util;

import io.army.lang.Nullable;

import java.util.*;

public abstract class _Collections {

    private _Collections() {
        throw new UnsupportedOperationException();
    }


    public static <E> List<E> emptyList() {
        return Collections.emptyList();
    }

    public static <T> List<T> unmodifiableList(List<T> list) {
        switch (list.size()) {
            case 0:
                list = Collections.emptyList();
                break;
            case 1:
                list = Collections.singletonList(list.get(0));
                break;
            default:
                list = Collections.unmodifiableList(list);
        }
        return list;

    }

    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        switch (map.size()) {
            case 0:
                map = Collections.emptyMap();
                break;
            case 1: {
                for (Map.Entry<K, V> e : map.entrySet()) {
                    map = Collections.singletonMap(e.getKey(), e.getValue());
                    break;
                }
            }
            break;
            default:
                map = Collections.unmodifiableMap(map);
        }
        return map;
    }

    public static <T> Set<T> unmodifiableSet(Set<T> set) {
        switch (set.size()) {
            case 0:
                set = Collections.emptySet();
                break;
            case 1: {
                for (T t : set) {
                    set = Collections.singleton(t);
                    break;
                }
            }
            break;
            default:
                set = Collections.unmodifiableSet(set);
        }
        return set;
    }


    public static boolean isEmpty(@io.qinarmy.lang.Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(@io.qinarmy.lang.Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <E> List<E> singletonList(E e) {
        return Collections.singletonList(e);
    }

    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        return Collections.singletonMap(key, value);
    }


    public static <K, V> HashMap<K, V> hashMap() {
        return new FinalHashMap<>();
    }

    public static <K, V> HashMap<K, V> hashMap(int initialCapacity) {
        return new FinalHashMap<>(initialCapacity);
    }

    public static <K, V> HashMap<K, V> hashMap(Map<? extends K, ? extends V> m) {
        return new FinalHashMap<>(m);
    }

    public static <K, V> HashMap<K, V> hashMapIgnoreKey(Object ignoreKey) {
        return new FinalHashMap<>();
    }

    public static <E> ArrayList<E> arrayList() {
        return new FinalArrayList<>();
    }

    public static <E> ArrayList<E> arrayList(int initialCapacity) {
        return new FinalArrayList<>(initialCapacity);
    }

    public static <E> ArrayList<E> arrayList(Collection<? extends E> c) {
        return new FinalArrayList<>(c);
    }

    public static <E> LinkedList<E> linkedList() {
        return new FinalLinkedArray<>();
    }

    public static <E> LinkedList<E> linkedList(Collection<? extends E> c) {
        return new FinalLinkedArray<>(c);
    }


    public static <T> List<T> safeUnmodifiableList(@Nullable List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        switch (list.size()) {
            case 0:
                list = Collections.emptyList();
                break;
            case 1:
                list = Collections.singletonList(list.get(0));
                break;
            default:
                list = Collections.unmodifiableList(list);
        }
        return list;

    }

    public static <K, V> Map<K, V> safeUnmodifiableMap(@Nullable Map<K, V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        switch (map.size()) {
            case 0:
                map = Collections.emptyMap();
                break;
            case 1: {
                for (Map.Entry<K, V> e : map.entrySet()) {
                    map = Collections.singletonMap(e.getKey(), e.getValue());
                    break;
                }
            }
            break;
            default:
                map = Collections.unmodifiableMap(map);
        }
        return map;
    }


    public static <T> List<T> safeList(@Nullable List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> List<T> asUnmodifiableList(final Collection<T> collection) {
        final List<T> list;
        switch (collection.size()) {
            case 0:
                list = Collections.emptyList();
                break;
            case 1: {
                if (collection instanceof List) {
                    list = Collections.singletonList(((List<T>) collection).get(0));
                } else {
                    List<T> temp = null;
                    for (T v : collection) {
                        temp = Collections.singletonList(v);
                        break;
                    }
                    list = temp;
                }

            }
            break;
            default: {
                list = Collections.unmodifiableList(new ArrayList<>(collection));
            }

        }
        return list;

    }


    /*############################## private method ######################################*/


    private static final class FinalHashMap<K, V> extends HashMap<K, V> {

        private FinalHashMap() {
        }

        private FinalHashMap(int initialCapacity) {
            super(initialCapacity);
        }

        private FinalHashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }

    }//FinalHashMap


    private static final class FinalArrayList<E> extends ArrayList<E> {

        private FinalArrayList() {
        }

        private FinalArrayList(int initialCapacity) {
            super(initialCapacity);
        }

        private FinalArrayList(Collection<? extends E> c) {
            super(c);
        }

    }//FinalArrayList


    private static final class FinalLinkedArray<E> extends LinkedList<E> {

        private FinalLinkedArray() {
        }

        private FinalLinkedArray(Collection<? extends E> c) {
            super(c);
        }

    }//FinalLinkedArray

    private static final class FinalHashSet<E> extends HashSet<E> {

        private FinalHashSet() {
        }

        private FinalHashSet(Collection<? extends E> c) {
            super(c);
        }

        private FinalHashSet(int initialCapacity) {
            super(initialCapacity);
        }

        private FinalHashSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }


    }//FinalHashSet


}
