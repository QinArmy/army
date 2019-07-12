package io.army.util;


/**
 * created  on 2018/9/12.
 */
public final class Pair<F, S> {


    private F first;

    private S second;

    public Pair() {

    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public Pair<F, S> setFirst(F first) {
        this.first = first;
        return this;
    }

    public S getSecond() {
        return second;
    }

    public Pair<F, S> setSecond(S second) {
        this.second = second;
        return this;
    }
}
