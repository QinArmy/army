package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SQLOperator;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;

import java.util.List;

final class UnaryExpression<E> extends AbstractExpression<E> {

    private final Expression<?> one;

    private final UnaryOperator unaryOperator;

    UnaryExpression(Expression<?> one, UnaryOperator unaryOperator) {
        this.one = one;
        this.unaryOperator = unaryOperator;
    }


    @Override
    public MappingType mappingType() {
        return one.mappingType();
    }


    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.stringBuilder();
        if (unaryOperator.position() == SQLOperator.Position.LEFT) {
            builder.append(unaryOperator.rendered())
                    .append(" ");
            one.appendSQL(context);
        } else if (unaryOperator.position() == SQLOperator.Position.RIGHT) {
            one.appendSQL(context);
            builder.append(unaryOperator.rendered())
                    .append(" ");
        } else {
            throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", unaryOperator));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (unaryOperator.position() == SQLOperator.Position.LEFT) {
            builder.append(unaryOperator.rendered())
                    .append(one);
        } else if (unaryOperator.position() == SQLOperator.Position.RIGHT) {
            builder.append(one)
                    .append(" ")
                    .append(unaryOperator.rendered());
        } else {
            throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", unaryOperator));
        }
        return builder.toString();
    }
}
