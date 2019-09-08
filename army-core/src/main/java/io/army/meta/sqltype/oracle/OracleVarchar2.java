package io.army.meta.sqltype.oracle;

import io.army.util.Precision;

/**
 * this class represent the {@code VARCHAR2} of oracle .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-0DC7FFAA-F03F-4448-8487-F2592496A510">Oracle 12 g</a></li>
 * </ul>
 */
public final class OracleVarchar2 extends OracleSQLDataType {

    private static final OracleVarchar2 INSTANCE = new OracleVarchar2();

    private OracleVarchar2() {

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
        return Precision.DEFAULT_CHAR_PRECISION;
    }

}
