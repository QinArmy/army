package io.army.function;

import io.army.criteria.ExpressionElement;

@FunctionalInterface
public interface ExpressionElementConsumer {

    ExpressionElementConsumer comma(ExpressionElement exp);

}
