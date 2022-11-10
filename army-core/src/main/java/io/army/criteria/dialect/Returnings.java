package io.army.criteria.dialect;

import io.army.criteria.Expression;
import io.army.criteria.NamedExpression;
import io.army.criteria.Selection;
import io.army.criteria.impl.SQLs;

import java.util.function.Supplier;

public interface Returnings {

    Returnings selection(Selection selection);

    Returnings selection(Expression expression, SQLs.WordAs wordAs, String alias);

    Returnings selection(Supplier<Selection> supplier);

    Returnings selection(NamedExpression exp1, NamedExpression exp2);

    Returnings selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

    Returnings selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);
}


