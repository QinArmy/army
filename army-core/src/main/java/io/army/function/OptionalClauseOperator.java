package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.SQLWords;

@FunctionalInterface
public interface OptionalClauseOperator<T1 extends SQLWords, T2, R> {

    R apply(Expression left, Expression right, T1 t1, T2 t2);


}
