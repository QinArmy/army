package io.army.meta.sqltype.mysql;

import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class MySQLInt extends MySQLDataType {

    public static final MySQLInt INSTANCE = new MySQLInt();

    private MySQLInt() {

    }

    @Override
    public DataKind dataKind() {
        return DataKind.INT;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        return String.format("INT(%s)", precision);
    }

    @Override
    protected String innerTypeName(int precision) {
        return innerTypeName(precision, 0);
    }

    @Override
    protected String innerTypeName() {
        return "INT";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_INT_PRECISION;
    }

}
