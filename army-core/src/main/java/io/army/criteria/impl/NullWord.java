package io.army.criteria.impl;

import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;

/**
 * <p>
 * This class representing sql {@code NULL} key word.
 * </p>
 *
 * @param <E> The java type The expression thant reference kwy word {@code NULL}
 */
final class NullWord<E> extends NoNOperationExpression<E> {

    static final NullWord<?> INSTANCE = new NullWord<>();


    private NullWord() {
    }

    @Override
    public void appendSql(_SqlContext context) {
        context.sqlBuilder().append(" NULL");
    }

    @Override
    public MappingType mappingType() {
        throw unsupportedOperation();
    }

    @Override
    public String toString() {
        return " NULL";
    }


}
