package io.army.criteria.impl;

import io.army.criteria.DualOperator;
import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SelfDescribed;
import io.army.dialect.ParamWrapper;
import io.army.util.Assert;

import java.util.Collection;
import java.util.Iterator;

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
    protected void afterSpace(SQLContext context) {
        left.appendSQL(context);
        DualOperator operator = DualOperator.IN;
        if (!in) {
            operator = DualOperator.NOT_IN;
        }
        StringBuilder builder = context.stringBuilder();
        builder.append(" ")
                .append(operator.rendered())
                .append(" ");

        if (expressionOrValues instanceof SelfDescribed) {
            ((SelfDescribed) expressionOrValues).appendSQL(context);
        } else if (expressionOrValues instanceof Collection) {
            doAppendCollection(context, left, (Collection<?>) expressionOrValues);
        } else {
            throw new IllegalArgumentException("expressionOrValues only Expression or Collection");
        }

    }


    private static void doAppendCollection(SQLContext context, Expression<?> left, Collection<?> collection) {
        StringBuilder builder = context.stringBuilder();
        builder.append("(");
        Object value;

        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); ) {
            value = iterator.next();
            Assert.notNull(value, "expressionOrValues has null");
            builder.append("?");
            context.appendParam(ParamWrapper.build(left.mappingType(), value));
            if (iterator.hasNext()) {
                builder.append(",");
            }

        }
        builder.append(")");
    }

    @Override
    public String beforeAs() {
        DualOperator operator = DualOperator.IN;
        if (!in) {
            operator = DualOperator.NOT_IN;
        }
        if (expressionOrValues instanceof Expression) {
            return operator.rendered() + " " + expressionOrValues;
        }
        StringBuilder builder = new StringBuilder(operator.rendered())
                .append(" (");
        collectionToString(builder, (Collection<?>) expressionOrValues);
        builder.append(")");

        return builder.toString();
    }

    private static void collectionToString(StringBuilder builder, Collection<?> collection) {
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
    }
}
