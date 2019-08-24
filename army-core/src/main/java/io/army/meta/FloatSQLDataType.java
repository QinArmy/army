package io.army.meta;

import io.army.dialect.Dialect;
import io.army.dialect.MySQL57Dialect;
import io.army.dialect.Oracle12Dialect;
import io.army.util.ArrayUtils;
import io.army.util.Precision;

import java.util.List;

/**
 * this class represent the {@code FLOAT}  .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-10D4D073-866D-4BD4-B3E9-ED153D505A6A">Oracle 12 g</a></li>
 * <li><a href="https://dev.mysql.com/doc/refman/5.7/en/floating-point-types.html">MySQL 5.7</a></li>
 * </ul>
 */
public final class FloatSQLDataType implements SQLDataType {

    public static final FloatSQLDataType INSTANCE = new FloatSQLDataType();

    private final List<Dialect> DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            MySQL57Dialect.INSTANCE,
            Oracle12Dialect.INSTANCE
    );

    @Override
    public String typeName() {
        return "FLOAT";
    }

    @Override
    public String typeName(int precision) {
        return "FLOAT(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }

    @Override
    public List<Dialect> dialectList() {
        return DIALECT_LIST;
    }
}
