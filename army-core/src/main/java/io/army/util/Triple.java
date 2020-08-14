package io.army.util;

/**
 * forbid Quadra
 *
 * @since 1.0
 */
public final class Triple<F, S, T> implements TripleBean {

    private final F first;

    private final S second;

    private final T third;

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }


}
