package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface PredicateWithMap {

      IPredicate apply(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

}
