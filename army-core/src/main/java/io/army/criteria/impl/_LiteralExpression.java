package io.army.criteria.impl;

import io.army.criteria.LiteralExpression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;

public interface _LiteralExpression extends _Expression, LiteralExpression {

    void appendSqlWithoutType(StringBuilder sqlBuilder, _SqlContext context);

}
