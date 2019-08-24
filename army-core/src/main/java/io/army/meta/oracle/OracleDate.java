package io.army.meta.oracle;


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
    public String typeName() {
        return "DATE";
    }

    @Override
    public String typeName(int precision) {
        return typeName();
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }
}
