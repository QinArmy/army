package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.TypeInfer;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ExpressionOperator<T extends TypeInfer, U, R extends Expression>
        extends BiFunction<BiFunction<T, U, Expression>, U, R> {


}
