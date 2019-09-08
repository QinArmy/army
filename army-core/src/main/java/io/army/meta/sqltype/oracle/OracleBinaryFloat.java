package io.army.meta.sqltype.oracle;

import io.army.util.Precision;

public class OracleBinaryFloat extends OracleSQLDataType {

    private static final Precision PRECISION = new Precision(22, 0);

    @Override
    public String typeName() {
        return "BINARY_FLOAT";
    }

    @Override
    public String typeName(int precision) {
        return "BINARY_FLOAT(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return PRECISION;
    }
}
