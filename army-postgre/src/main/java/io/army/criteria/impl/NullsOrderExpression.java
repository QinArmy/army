package io.army.criteria.impl;

interface NullsOrderExpression<E> extends SortExpression<E> {

    Nulls nulls();

    enum Nulls {
        FIRST,
        LAST
    }
}
