package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;

import java.util.List;

 final class DualExpresion<E> extends AbstractExpression<E>  {

    protected final Expression<?> left;

    protected final DualOperator operator;

    protected final Expression<?> right;


    DualExpresion(Expression<?> left, DualOperator operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    @Override
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        left.appendSQL(builder,paramWrapperList);
        builder.append(" ");
        builder.append(operator.rendered());
        builder.append(" ");
        right.appendSQL(builder,paramWrapperList);
    }

}
