package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;

import java.util.List;

final class UnaryExpression<E> extends AbstractExpression<E> {

    private final Expression<?> one;

    private final UnaryOperator unaryOperator;

    UnaryExpression(Expression<?> one, UnaryOperator unaryOperator) {
        this.one = one;
        this.unaryOperator = unaryOperator;
    }

    @Override
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        if (unaryOperator.position() == SQLOperator.Position.LEFT) {
            builder.append(unaryOperator.rendered())
                    .append(" ");
            one.appendSQL(builder, paramWrapperList);
        } else if (unaryOperator.position() == SQLOperator.Position.RIGHT) {
            one.appendSQL(builder, paramWrapperList);
            builder.append(unaryOperator.rendered())
                    .append(" ");
        } else {
            throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", unaryOperator));
        }
    }
}
