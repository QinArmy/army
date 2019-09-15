package io.army.meta.sqltype.mysql;

import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class MySQLDate extends MySQLDataType {

    public static final MySQLDate INSTANCE = new MySQLDate();

    private MySQLDate() {

    }

    @Override
    public DataKind dataKind() {
        return DataKind.DATE;
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
        return "DATE";
    }


    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }

}
