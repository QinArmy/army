package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.util.List;

final class UnaryPredicate extends AbstractPredicate {

    private final UnaryOperator operator;

    private final Object expressionOrConstant;

    UnaryPredicate(UnaryOperator operator, Object expressionOrConstant) {
        Assert.notNull(expressionOrConstant, "expressionOrConstant required");

        this.operator = operator;
        this.expressionOrConstant = expressionOrConstant;
    }

    @Override
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        switch (operator.position()) {
            case LEFT:
                builder.append(operator.rendered());
                doAppend(builder, paramWrapperList);
                break;
            case RIGHT:
                doAppend(builder, paramWrapperList);
                builder.append(operator.rendered());
                break;
            default:
                throw new IllegalStateException(String.format("UnaryOperator[%s]'s position error.", operator));
        }
    }

    private void doAppend(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        if (expressionOrConstant instanceof Expression) {
            ((Expression<?>) expressionOrConstant).appendSQL(builder, paramWrapperList);
        } else {
            builder.append("?");
            MappingType mappingType = MappingFactory.getDefaultMapping(expressionOrConstant.getClass());
            paramWrapperList.add(ParamWrapper.build(mappingType, expressionOrConstant));
        }
    }
}
