/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    public static <T> List<T> unmodifiableListForDeveloper(List<T> list) {
        return unmodifiableList(list);
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

    public static <K, V> Map<K, V> unmodifiableMapForDeveloper(Map<K, V> map) {
        return unmodifiableMap(map);
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


    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
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

    public static <K, V> HashMap<K, V> hashMapForSize(int initialSize) {
        return new FinalHashMap<>((int) (initialSize / 0.75F));
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

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
        return new FinalConcurrentHashMap<>();
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(int initialCapacity) {
        return new FinalConcurrentHashMap<>(initialCapacity);
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

    public static <E> ArrayList<E> secondQueryList() {
        return new SecondQueryArrayList<>();
    }

    public static boolean isSecondQueryList(List<?> list) {
        return list instanceof SecondQueryArrayList;
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
                list = emptyList();
                break;
            case 1: {
                if (collection instanceof List) {
                    list = singletonList(((List<T>) collection).get(0));
                } else {
                    List<T> temp = null;
                    for (T v : collection) {
                        temp = singletonList(v);
                        break;
                    }
                    list = temp;
                }

            }
            break;
            default: {
                list = unmodifiableList(arrayList(collection));
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

    private static final class SecondQueryArrayList<E> extends ArrayList<E> {

        private SecondQueryArrayList() {
        }

    } // SecondQueryArrayList


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

    private static final class FinalConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

        private FinalConcurrentHashMap() {
        }

        private FinalConcurrentHashMap(int initialCapacity) {
            super(initialCapacity);
        }
    }//FinalConcurrentHashMap


}
