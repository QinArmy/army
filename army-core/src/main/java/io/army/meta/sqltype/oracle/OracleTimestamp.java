package io.army.meta.sqltype.oracle;

import io.army.util.Precision;

public final class OracleTimestamp extends OracleSQLDataType {

    static final Precision PRECISION = new Precision(6, 0);

    @Override
    public String typeName() {
        return "TIMESTAMP";
    }

    @Override
    public String typeName(int precision) {
        return "TIMESTAMP(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return PRECISION;
    }
}
