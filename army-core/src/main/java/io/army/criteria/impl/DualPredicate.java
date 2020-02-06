package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.util.StringUtils;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * created  on 2018/11/25.
 */
final class DualPredicate extends AbstractPredicate {

    private final Expression<?> left;

    private final DualOperator operator;

    private final Expression<?> right;


    DualPredicate(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    @Override
    protected void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        left.appendSQL(sql,builder,paramWrapperList);
        builder.append(" ");
        builder.append(operator.rendered());
        builder.append(" ");
        right.appendSQL(sql,builder,paramWrapperList);
    }



}
