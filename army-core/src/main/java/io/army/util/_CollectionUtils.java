package io.army.util;

import io.army.lang.Nullable;

import java.util.*;

public abstract class _CollectionUtils extends io.qinarmy.util.CollectionUtils {

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


}
