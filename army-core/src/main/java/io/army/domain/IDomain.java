package io.army.domain;

import io.army.annotation.Column;
import io.army.meta.FieldMeta;

/**
 *
 */
public interface IDomain {


    /**
     * @see Column#defaultValue()
     */
    String NOW = "NOW()";

    /**
     * @see Column#defaultValue()
     * @see io.army.dialect.DDLUtils#zeroForTimeType(FieldMeta)
     */
    String ZERO_TIME = "$ZERO_TIME$";

    /**
     * @see Column#defaultValue()
     * @see io.army.dialect.DDLUtils#zeroForTimeType(FieldMeta)
     */
    String ZERO_DATE = "$ZERO_DATE$";

    /**
     * @see Column#defaultValue()
     * @see io.army.dialect.DDLUtils#zeroForTimeType(FieldMeta)
     */
    String ZERO_YEAR = "$ZERO_YEAR$";

    /**
     * @see Column#defaultValue()
     * @see io.army.dialect.DDLUtils#zeroForTimeType(FieldMeta)
     */
    String ZERO_DATE_TIME = "$ZERO_DATE_TIME$";

    /**
     * @see Column#defaultValue()
     */
    String ZERO = "0";

    /**
     * @see Column#defaultValue()
     */
    String ONE = "1";

    /**
     * @see Column#defaultValue()
     */
    String DECIMAL_ZERO = "0.00";

    /**
     * @see Column#defaultValue()
     */
    String N = "N";

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
