package io.army.criteria;

final class BetweenWrapperImpl<E> implements BetweenWrapper<E> {

    private final Expression<E> first;

    private final Expression<E> second;

    BetweenWrapperImpl(Expression<E> first, Expression<E> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Expression<E> first() {
        return this.first;
    }

    @Override
    public Expression<E> second() {
        return this.second;
    }
}
