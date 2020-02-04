package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;

import java.util.List;

final class BetweenConstantPredicate extends AbstractPredicate {

    private final Expression<?> left;

    private final Object center;

    private final Object right;

    BetweenConstantPredicate(Expression<?> left, Object center, Object right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    @Override
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        left.appendSQL(builder, paramWrapperList);
        builder.append(" BETWEEN");
        if (center instanceof Expression) {
            ((Expression<?>) center).appendSQL(builder, paramWrapperList);
        } else {
            builder.append(" ? ");
            paramWrapperList.add(ParamWrapper.build(left.mappingType(), center));
        }
        builder.append("AND");
        if (right instanceof Expression) {
            ((Expression<?>) right).appendSQL(builder, paramWrapperList);
        } else {
            builder.append(" ? ");
            paramWrapperList.add(ParamWrapper.build(left.mappingType(), right));
        }

    }
}
