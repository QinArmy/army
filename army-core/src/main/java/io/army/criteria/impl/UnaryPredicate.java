package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.util.List;

final class UnaryPredicate extends AbstractPredicate {

    private final UnaryOperator operator;

    private final Expression<?> expression;

    UnaryPredicate(UnaryOperator operator, Expression<?> expression) {
        Assert.notNull(expression, "expression required");

        this.operator = operator;
        this.expression = expression;
    }

    @Override
    protected void appendSQLBeforeWhitespace(SQL sql, StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        switch (operator.position()) {
            case LEFT:
                builder.append(operator.rendered());
                expression.appendSQL(sql, builder, paramWrapperList);
                break;
            case RIGHT:
                expression.appendSQL(sql, builder, paramWrapperList);
                builder.append(operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", operator));
        }
    }

}
