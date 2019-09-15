package io.army.meta.sqltype.oracle;

import io.army.meta.sqltype.DataKind;
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
    public DataKind dataKind() {
        return DataKind.TEXT;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        return null;
    }

    @Override
    protected String innerTypeName(int precision) {
        return null;
    }

    @Override
    protected String innerTypeName() {
        return null;
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }


}
