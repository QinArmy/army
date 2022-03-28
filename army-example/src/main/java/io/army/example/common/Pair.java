package io.army.example.common;

import io.army.bean.PairBean;

public final class Pair<F, S> implements PairBean {

    public final F first;

    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }


}
