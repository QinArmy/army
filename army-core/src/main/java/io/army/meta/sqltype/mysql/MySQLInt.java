package io.army.meta.sqltype.mysql;

import io.army.util.Precision;

public final class MySQLInt extends AbstractMySQLDataType {

    public static final MySQLInt INSTANCE = new MySQLInt();

    private MySQLInt() {

    }

    @Override
    public String typeName() {
        return "INT";
    }

    @Override
    public String typeName(int precision) {
        return "INT(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_INT_PRECISION;
    }

}
