package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.SQLs;


public interface BetweenOperator {

    IPredicate apply(Expression first, SQLs.WordAnd and, Expression second);


}
