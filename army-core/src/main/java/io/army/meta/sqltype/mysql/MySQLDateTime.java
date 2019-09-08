package io.army.meta.sqltype.mysql;

import io.army.util.Precision;

public final class MySQLDateTime extends AbstractMySQLDataType {

    public static final MySQLDateTime INSTANCE = new MySQLDateTime();

    private MySQLDateTime() {

    }

    @Override
    public String typeName() {
        return "DATETIME";
    }

    @Override
    public String typeName(int precision) {
        return "DATETIME(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }

}
