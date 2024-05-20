package io.army.criteria.impl;

public final class _Triple<F, S, T> {


    public static <F, S, T> _Triple<F, S, T> create(F first, S second, T third) {
        return new _Triple<>(first, second, third);
    }

    public final F first;

    public final S second;

    public final T third;

    /**
     * private constructor
     */
    private _Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }


}
