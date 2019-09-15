package io.army.meta.sqltype.oracle;


import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

/**
 * this class represent the {@code DATE} of oracle ,
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-5405B652-C30E-4F4F-9D33-9A4CB2110F1B">Oracle 12 g</a></li>
 * </ul>
 */
public final class OracleDate extends OracleSQLDataType {

    private OracleDate() {
    }

    @Override
    public DataKind dataKind() {
        return DataKind.DATE;
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
        return Precision.EMPTY;
    }
}
