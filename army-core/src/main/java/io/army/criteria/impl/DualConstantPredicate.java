package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;

import java.sql.JDBCType;
import java.util.List;

/**
 * created  on 2018/11/27.
 */
final class DualConstantPredicate extends AbstractPredicate {


    private final Expression<?> left;

    private final DualOperator operator;

    private final Object right;

    DualConstantPredicate(Expression<?> left, DualOperator operator, Object right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        builder.append("( ");
        left.appendSQL(builder, paramWrapperList);
        builder.append(" ");
        builder.append(operator.rendered());
        builder.append(" ? ");
        builder.append(")");
    }
}
