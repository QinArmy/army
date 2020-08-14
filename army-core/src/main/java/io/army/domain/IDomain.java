package io.army.domain;

import io.army.annotation.Column;

/**
 *
 */
public interface IDomain {


    /**
     * see {@code see io.army.sqldatatype.SQLDataType#nowValue(FieldMeta, SQLBuilder, Database)}
     *
     * @see Column#defaultValue()
     */
    String NOW = "$NOW()$";

    /**
     * see {@code see io.army.sqldatatype.SQLDataType#zeroValue(FieldMeta, SQLBuilder, Database)}
     *
     * @see Column#defaultValue()
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
