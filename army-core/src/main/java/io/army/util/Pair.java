package io.army.util;


/**
 * @since 1.0
 */
public final class Pair<F, S> implements PairBean {


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
