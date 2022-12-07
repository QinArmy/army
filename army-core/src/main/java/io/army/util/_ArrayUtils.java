package io.army.util;

import io.army.lang.NonNull;
import io.army.lang.Nullable;

import java.util.*;

public abstract class _ArrayUtils {

    protected _ArrayUtils() {
        throw new UnsupportedOperationException();
    }

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
        } else if (e.length == 1) {
            list = Collections.singletonList(e[0]);
        } else {
            list = new ArrayList<>(e.length);
            Collections.addAll(list, e);
            list = Collections.unmodifiableList(list);
        }
        return list;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> unmodifiableListOf(T first, T... rest) {
        final List<T> list;
        if (rest.length == 0) {
            list = Collections.singletonList(first);
        } else {
            final List<T> temp = new ArrayList<>(1 + rest.length);
            temp.add(first);
            Collections.addAll(temp, rest);
            list = Collections.unmodifiableList(temp);
        }
        return list;
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


    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> asUnmodifiableList(Collection<T> collection, @Nullable T... addElements) {
        if (collection instanceof List
                && (addElements == null || addElements.length == 0)) {
            return Collections.unmodifiableList((List<T>) collection);
        }
        return Collections.unmodifiableList(asList(collection, addElements));
    }


}
