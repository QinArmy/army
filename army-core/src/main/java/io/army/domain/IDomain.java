package io.army.domain;

import io.army.annotation.Column;
import io.army.dialect.Database;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;

/**
 *
 */
public interface IDomain {


    /**
     * @see Column#defaultValue()
     * @see io.army.sqltype.SQLDataType#nowValue(FieldMeta, SQLBuilder, Database)
     */
    String NOW = "$NOW()$";

    /**
     * @see Column#defaultValue()
     * @see io.army.sqltype.SQLDataType#zeroValue(FieldMeta, SQLBuilder, Database)
     */
    String ZERO_VALUE = "$ZERO_VALUE$";

    /**
     * @see Column#defaultValue()
     */
    String ONE = "1";

    /**
     * @see Column#defaultValue()
     */
    String Y = "Y";

    /**
     * @see io.army.annotation.Table#charset()
     */
    String UTF_8 = "UTF-8";

    Object getId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

}
