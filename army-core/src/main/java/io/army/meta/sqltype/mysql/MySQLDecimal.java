package io.army.meta.sqltype.mysql;

import io.army.util.Precision;

public final class MySQLDecimal extends AbstractMySQLDataType {

    public static final MySQLDecimal INSTANCE = new MySQLDecimal();

    private MySQLDecimal() {

    }

    @Override
    public String typeName() {
        return "DECIMAL";
    }

    @Override
    public String typeName(int precision) {
        return "DECIMAL(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_DECIMAL_PRECISION;
    }
}
