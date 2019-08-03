package io.army.meta;

import io.army.criteria.dialect.Dialect;
import io.army.criteria.dialect.Oracle12Dialect;
import io.army.util.ArrayUtils;
import io.army.util.Precision;

import java.util.List;

/**
 * this class represent the {@code VARCHAR2} of oracle .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-0DC7FFAA-F03F-4448-8487-F2592496A510">Oracle 12 g</a></li>
 * </ul>
 */
public final class Varchar2SQLDateType implements SQLType {

    private static final Varchar2SQLDateType INSTANCE = new Varchar2SQLDateType();

    private static final Precision DEFAULT_PRECISION = new Precision(255, null);

    private static final List<Dialect> SUPPORT_DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            Oracle12Dialect.INSTANCE
    );

    private Varchar2SQLDateType() {

    }

    @Override
    public String typeName() {
        return "VARCHAR2";
    }

    @Override
    public String typeName(int precision) {
        return "VARCHAR2(" + precision + ")";
    }

    @Override
    public String typeName(int precision, int scale) {
        return typeName(precision);
    }

    @Override
    public Precision defaultPrecision() {
        return DEFAULT_PRECISION;
    }

    @Override
    public List<Dialect> dialectList() {
        return SUPPORT_DIALECT_LIST;
    }
}
