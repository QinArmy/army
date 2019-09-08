package io.army.meta.sqltype.mysql;

import io.army.util.Precision;

public final class MySQLBigInt extends AbstractMySQLDataType {

    public static final MySQLBigInt INSTANCE = new MySQLBigInt();

    private MySQLBigInt() {
    }

    @Override
    public String typeName() {
        return "BIGINT";
    }

    @Override
    public String typeName(int precision) {
        return "BIGINT(" + precision + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_BIGINT_PRECISION;
    }
}
