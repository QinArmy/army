package io.army.criteria;


import java.util.function.Function;

/**
 * @param <E> between expression's Java Type
 * @see Expression#between(Function)
 */
public interface ExpressionPair {

    Expression first();

    Expression second();

}
