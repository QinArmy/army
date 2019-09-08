package io.army.meta.sqltype.mysql;

import io.army.util.Precision;

public final class MySQLYear extends AbstractMySQLDataType {

    public static final MySQLYear INSTANCE = new MySQLYear();

    private MySQLYear() {

    }

    @Override
    public String typeName() {
        return "YEAR";
    }

    @Override
    public String typeName(int precision) {
        return "YEAR(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }
}
