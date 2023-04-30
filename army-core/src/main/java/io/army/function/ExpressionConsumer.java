package io.army.function;

import io.army.criteria.Expression;

@FunctionalInterface
public interface ExpressionConsumer {

    ExpressionConsumer comma(Expression exp);

}
