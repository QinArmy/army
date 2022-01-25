package io.army.criteria;

final class BetweenWrapperImpl implements BetweenWrapper {

    private final Expression first;

    private final Expression second;

    BetweenWrapperImpl(Expression first, Expression second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Expression first() {
        return this.first;
    }

    @Override
    public Expression second() {
        return this.second;
    }
}
