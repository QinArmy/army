package io.army.meta.sqltype.oracle;

import io.army.util.Precision;

public class OracleTimestampWithZone extends OracleSQLDataType {

    @Override
    public String typeName() {
        return "TIMESTAMP WITH TIME ZONE";
    }

    @Override
    public String typeName(int precision) {
        return "TIMESTAMP(" + precision + ")WITH TIME ZONE";
    }

    @Override
    public Precision defaultPrecision() {
        return OracleTimestamp.PRECISION;
    }
}
