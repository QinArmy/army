package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl._ItemExpression;

import java.util.function.BiFunction;
import java.util.function.Function;

@Deprecated
@FunctionalInterface
public interface ScalarFunction<E extends Expression, I extends Item, R extends Query._SelectClauseOfQuery>
        extends BiFunction<Function<TypeInfer, I>, Function<_ItemExpression<I>, E>, R> {

}
