package io.army.meta.sqltype.mysql;

import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class MySQLBigInt extends MySQLDataType {

    public static final MySQLBigInt INSTANCE = new MySQLBigInt();

    private MySQLBigInt() {
    }

    @Override
    public DataKind dataKind() {
        return DataKind.INT;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        return String.format("BIGINT(%s,%s)", precision, scale);
    }

    @Override
    protected String innerTypeName(int precision) {
        return String.format("BIGINT(%s)", precision);
    }

    @Override
    protected String innerTypeName() {
        return "BIGINT";
    }


    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_BIGINT_PRECISION;
    }


}
