package org.qinarmy.army.util;

import java.util.*;

/**
 * created  on 31/03/2018.
 */
public abstract class ArrayUtils {


    public static <T> Set<T> asSet(Collection<T> collection, T... e) {
        Set<T> set = new HashSet<>(collection);
        if (e != null) {
            Collections.addAll(set, e);
        }
        return set;
    }


    public static <T> Set<T> asSet(T... e) {
        return asSet(Collections.emptySet(), e);
    }


    public static <T> Set<T> asUnmodifiableSet(Collection<T> collection, T... e) {
        return Collections.unmodifiableSet(asSet(collection, e));
    }

    public static <T> Set<T> asUnmodifiableSet(T... e) {
        return asUnmodifiableSet(Collections.emptySet(), e);
    }

    public static <T> List<T> asList(Collection<T> collection, T... e) {
        List<T> list;
        int size = collection.size();
        if (e != null) {
            size += e.length;
        }
        list = new ArrayList<>(size);
        list.addAll(collection);

        if (e != null) {
            Collections.addAll(list, e);
        }
        return list;
    }


    public static <T> List<T> asUnmodifiableList(T... e) {
        return Collections.unmodifiableList(asList(Collections.emptyList(), e));
    }


}
