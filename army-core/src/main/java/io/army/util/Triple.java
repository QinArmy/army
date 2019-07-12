package io.army.util;

/**
 * forbid Quadra
 * created  on 2018/11/24.
 */
public final class Triple<F, S, T> {

    private F first;

    private S second;

    private T third;

    public Triple() {
    }


    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public F getFirst() {
        return first;
    }

    public Triple<F, S, T> setFirst(F first) {
        this.first = first;
        return this;
    }

    public S getSecond() {
        return second;
    }

    public Triple<F, S, T> setSecond(S second) {
        this.second = second;
        return this;
    }

    public T getThird() {
        return third;
    }

    public Triple<F, S, T> setThird(T third) {
        this.third = third;
        return this;
    }
}
