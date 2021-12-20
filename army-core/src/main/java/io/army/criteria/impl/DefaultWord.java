package io.army.criteria.impl;

import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;

/**
 * <p>
 * This class representing sql {@code DEFAULT} key word.
 * </p>
 *
 * @param <E> The java type The expression thant reference kwy word {@code DEFAULT}
 */
public final class DefaultWord<E> extends NoNOperationExpression<E> {

    static DefaultWord<?> INSTANCE = new DefaultWord<>();


    private DefaultWord() {
    }


    @Override
    public MappingType mappingType() {
        throw unsupportedOperation();
    }

    @Override
    public void appendSql(final _SqlContext context) {
        context.sqlBuilder().append(" DEFAULT");
    }

    @Override
    public String toString() {
        return " DEFAULT";
    }


}
