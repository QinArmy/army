package io.army.criteria;

import java.util.function.Function;

/**
 * @param <E> between expression's Java Type
 * @see Expression#between(Function)
 */
public interface BetweenWrapper<E> {

    Expression<E> first();

    Expression<E> second();

    static <E> BetweenWrapper<E> build(Expression<E> first, Expression<E> second) {
        return new BetweenWrapperImpl<>(first, second);
    }

}
