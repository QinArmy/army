package io.army.meta.oracle;

import io.army.util.Precision;

public class OracleBinaryDouble extends OracleSQLDataType {

    private static final Precision PRECISION = new Precision(53, null);

    @Override
    public String typeName() {
        return "BINARY_DOUBLE";
    }

    @Override
    public String typeName(int precision) {
        return "BINARY_DOUBLE(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return PRECISION;
    }
}
