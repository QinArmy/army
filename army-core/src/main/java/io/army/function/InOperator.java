package io.army.function;

import io.army.criteria.IPredicate;
import io.army.criteria.RowExpression;
import io.army.criteria.SimpleExpression;

import java.util.Collection;
import java.util.function.BiFunction;

public interface InOperator {

    IPredicate apply(BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value);

}
