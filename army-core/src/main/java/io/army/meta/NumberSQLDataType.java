package io.army.meta;

import io.army.criteria.dialect.Dialect;
import io.army.criteria.dialect.Oracle12Dialect;
import io.army.util.ArrayUtils;
import io.army.util.Precision;

import java.util.List;

import static io.army.util.Precision.DEFAULT_DECIMAL_PRECISION;


/**
 * this class represent the {@code NUMBER} of oracle .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-75209AF6-476D-4C44-A5DC-5FA70D701B78">Oracle 12 g</a></li>
 * </ul>
 */
public final class NumberSQLDataType implements SQLType {

    private static final NumberSQLDataType INSTANCE = new NumberSQLDataType();

    private static final List<Dialect> SUPPORT_DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            Oracle12Dialect.INSTANCE
    );

    private NumberSQLDataType() {

    }

    @Override
    public String typeName() {
        return "NUMBER";
    }

    @Override
    public String typeName(int precision) {
        return typeName(precision, 0);
    }

    @Override
    public String typeName(int precision, int scale) {
        return "NUMBER(" + precision + "," + scale + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return DEFAULT_DECIMAL_PRECISION;
    }

    @Override
    public List<Dialect> dialectList() {
        return SUPPORT_DIALECT_LIST;
    }
}
