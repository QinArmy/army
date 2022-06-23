package io.army.criteria.impl;

public final class _Pair<F, S> {

    static <F, S> _Pair<F, S> create(F first, S second) {
        return new _Pair<>(first, second);
    }

    public final F first;

    public final S second;

    private _Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }


}
