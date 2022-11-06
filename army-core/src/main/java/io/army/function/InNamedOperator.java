package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;

@FunctionalInterface
public interface InNamedOperator {


    IPredicate apply(TeNamedOperator<Expression> namedOperator, String paramName, int size);


}
