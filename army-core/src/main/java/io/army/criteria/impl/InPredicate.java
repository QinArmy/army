package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @see DualOperator#IN
 * @see DualOperator#NOT_IN
 */
final class InPredicate extends AbstractPredicate {

    private final boolean in;

    private final Expression<?> left;

    private final Object expressionOrValues;

    public InPredicate(boolean in, Expression<?> left, Object expressionOrValues) {
        this.in = in;
        this.left = left;
        if (expressionOrValues instanceof Expression
                || expressionOrValues instanceof Collection) {
            this.expressionOrValues = expressionOrValues;
        } else {
            throw new IllegalArgumentException("expressionOrValues only Expression or Collection");
        }
    }

    @Override
    protected void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        left.appendSQL(sql,builder, paramWrapperList);
        DualOperator operator = DualOperator.IN;
        if (!in) {
            operator = DualOperator.NOT_IN;
        }
        builder.append(" ");
        builder.append(operator.rendered());
        builder.append(" ");

        if (expressionOrValues instanceof Expression) {
            ((Expression<?>) expressionOrValues).appendSQL(sql,builder, paramWrapperList);
        } else if (expressionOrValues instanceof Collection) {
            doAppendCollection(builder, paramWrapperList, left, (Collection<?>) expressionOrValues);
        } else {
            throw new IllegalArgumentException("expressionOrValues only Expression or Collection");
        }

    }

    private static void doAppendCollection(StringBuilder builder, List<ParamWrapper> paramWrapperList
            , Expression<?> left
            , Collection<?> collection) {

        builder.append("(");
        Object value;

        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); ) {
            value = iterator.next();
            Assert.notNull(value, "expressionOrValues has null");
            builder.append("?");
            paramWrapperList.add(ParamWrapper.build(left.mappingType(), value));
            if (iterator.hasNext()) {
                builder.append(",");
            }

        }
        builder.append(")");
    }

}
