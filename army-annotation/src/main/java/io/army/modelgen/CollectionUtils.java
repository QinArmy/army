package io.army.modelgen;

import io.army.lang.NonNull;
import io.army.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

abstract class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }


    static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asUnmodifiableSet(@Nullable T... e) {
        final Set<T> set;
        if (e == null || e.length == 0) {
            set = Collections.emptySet();
        } else if (e.length == 1) {
            set = Collections.singleton(e[0]);
        } else {
            final Set<T> temp = new HashSet<>((int) (e.length / 0.75F));
            Collections.addAll(temp, e);
            set = Collections.unmodifiableSet(temp);
        }
        return set;
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asUnmodifiableSet(Collection<T> collection, @Nullable T... e) {
        final Set<T> set;
        if (collection.isEmpty()) {
            set = asUnmodifiableSet(e);
        } else if (e == null || e.length == 0) {
            if (collection instanceof Set) {
                set = Collections.unmodifiableSet((Set<T>) collection);
            } else {
                set = Collections.unmodifiableSet(new HashSet<>(collection));
            }
        } else {
            final Set<T> temp = new HashSet<>((int) ((e.length + collection.size()) / 0.75F));
            temp.addAll(collection);
            Collections.addAll(temp, e);
            set = Collections.unmodifiableSet(temp);
        }
        return set;
    }


}
