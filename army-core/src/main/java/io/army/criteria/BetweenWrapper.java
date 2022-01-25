package io.army.criteria;

import java.util.function.Function;

/**
 * @param <E> between expression's Java Type
 * @see Expression#between(Function)
 */
public interface BetweenWrapper {

    Expression first();

    Expression second();

    static BetweenWrapper build(Expression first, Expression second) {
        return new BetweenWrapperImpl(first, second);
    }

}
