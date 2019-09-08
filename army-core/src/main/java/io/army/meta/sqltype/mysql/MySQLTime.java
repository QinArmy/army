package io.army.meta.sqltype.mysql;

import io.army.util.Precision;

public final class MySQLTime extends AbstractMySQLDataType {

    public static final MySQLTime INSTANCE = new MySQLTime();

    private MySQLTime() {

    }

    @Override
    public String typeName() {
        return "TIME";
    }

    @Override
    public String typeName(int precision) {
        return "TIME(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }
}
