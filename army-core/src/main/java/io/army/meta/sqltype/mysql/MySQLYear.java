package io.army.meta.sqltype.mysql;

import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class MySQLYear extends MySQLDataType {

    public static final MySQLYear INSTANCE = new MySQLYear();

    private MySQLYear() {

    }

    @Override
    public DataKind dataKind() {
        return DataKind.YEAR;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        return innerTypeName();
    }

    @Override
    protected String innerTypeName(int precision) {
        return innerTypeName();
    }

    @Override
    protected String innerTypeName() {
        return "YEAR";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }
}
