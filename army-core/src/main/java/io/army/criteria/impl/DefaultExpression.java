package io.army.criteria.impl;

import java.sql.JDBCType;

/**
 * created  on 2018/11/25.
 */
class DefaultExpression<E> extends AbstractExpression<E> {

    public DefaultExpression() {
    }

    @Override
    public Class<?> javaType() {
        return null;
    }

    @Override
    public JDBCType jdbcType() {
        return null;
    }
}
