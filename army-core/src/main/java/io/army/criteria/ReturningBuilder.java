package io.army.criteria;

import io.army.criteria.impl.SQLs;

import java.util.function.Supplier;

public interface ReturningBuilder {

    ReturningBuilder selection(Selection selection);

    ReturningBuilder selection(Expression expression, SQLs.WordAs wordAs, String alias);

    ReturningBuilder selection(Supplier<Selection> supplier);

    ReturningBuilder selection(NamedExpression exp1, NamedExpression exp2);

    ReturningBuilder selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

    ReturningBuilder selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);
}


