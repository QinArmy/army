package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;

import java.util.List;

final class BetweenPredicate extends AbstractPredicate {

    private final Expression<?> left;

    private final Expression<?> center;

    private final Expression<?> right;

    BetweenPredicate(Expression<?> left, Expression<?> center, Expression<?> right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }


    @Override
    protected void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        left.appendSQL(sql,builder, paramWrapperList);
        builder.append(" BETWEEN ");
        center.appendSQL(sql,builder,paramWrapperList);
        builder.append(" AND ");
        right.appendSQL(sql,builder,paramWrapperList);
    }

}
