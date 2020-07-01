package io.army.util;


/**
 * created  on 2018/9/12.
 */
public final class Pair<F, S> implements PairBean<F, S> {


    private final F first;

    private final S second;


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
