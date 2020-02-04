package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;

import java.util.List;

/**
 * created  on 2018/11/25.
 */
enum TernaryOperator implements SQLOperator {

    BETWEEN {
        @Override
        public TernaryOperator negated() {
            return null;
        }

        @Override
        public String rendered() {
            return "%s BETWEEN %s AND %s";
        }

        @Override
        void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList
                , Expression<?> left, Expression<?> center, Expression<?> right) {
            left.appendSQL(builder, paramWrapperList);
            builder.append(" BETWEEN ");
            center.appendSQL(builder, paramWrapperList);
            builder.append(" AND ");
            right.appendSQL(builder, paramWrapperList);
        }

    };


    @Override
    public final Position position() {
        return Position.TWO;
    }

    abstract void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList
            , Expression<?> left, Expression<?> center, Expression<?> right);
}
