package io.army.meta.oracle;

import io.army.util.Precision;

/**
 * this class represent the {@code VARCHAR2} of oracle .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-0DC7FFAA-F03F-4448-8487-F2592496A510">Oracle 12 g</a></li>
 * </ul>
 */
public final class OracleNVarchar2 extends OracleSQLDataType {

    private static final OracleNVarchar2 INSTANCE = new OracleNVarchar2();

    private OracleNVarchar2() {

    }

    @Override
    public String typeName() {
        return "NVARCHAR2";
    }

    @Override
    public String typeName(int precision) {
        return "NVARCHAR2(" + precision + ")";
    }


    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }


}
