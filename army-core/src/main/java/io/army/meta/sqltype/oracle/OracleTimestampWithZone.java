package io.army.meta.sqltype.oracle;

import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class OracleTimestampWithZone extends OracleSQLDataType {

    @Override
    public DataKind dataKind() {
        return DataKind.DATE_TIME;
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
        return OracleTimestamp.PRECISION;
    }
}
