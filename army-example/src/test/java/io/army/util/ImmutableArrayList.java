package io.army.util;

import io.army.type.ImmutableSpec;

import java.util.ArrayList;
import java.util.Collection;

public final class ImmutableArrayList<E> extends ArrayList<E> implements ImmutableSpec {

    public static <E> ImmutableArrayList<E> arrayList() {
        return new ImmutableArrayList<>();
    }

    public static <E> ImmutableArrayList<E> arrayList(int initialCapacity) {
        return new ImmutableArrayList<>(initialCapacity);
    }

    public static <E> ImmutableArrayList<E> arrayList(Collection<? extends E> c) {
        return new ImmutableArrayList<>(c);
    }


    private ImmutableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    private ImmutableArrayList() {
    }

    private ImmutableArrayList(Collection<? extends E> c) {
        super(c);
    }


}
