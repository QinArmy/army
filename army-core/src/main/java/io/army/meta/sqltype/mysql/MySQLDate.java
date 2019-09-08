package io.army.meta.sqltype.mysql;

import io.army.util.Precision;

public final class MySQLDate extends AbstractMySQLDataType {

    public static final MySQLDate INSTANCE = new MySQLDate();

    private MySQLDate() {

    }

    @Override
    public String typeName() {
        return "DATE";
    }

    @Override
    public String typeName(int precision) {
        return "DATE(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }

}
