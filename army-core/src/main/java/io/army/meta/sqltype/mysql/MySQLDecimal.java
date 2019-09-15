package io.army.meta.sqltype.mysql;

import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class MySQLDecimal extends MySQLDataType {

    public static final MySQLDecimal INSTANCE = new MySQLDecimal();

    private MySQLDecimal() {

    }

    @Override
    public DataKind dataKind() {
        return DataKind.DECIMAL;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        return String.format("DECIMAL(%s,%s)", precision, scale);
    }

    @Override
    protected String innerTypeName(int precision) {
        return innerTypeName(precision, 0);
    }

    @Override
    protected String innerTypeName() {
        Precision p = defaultPrecision();
        return innerTypeName(p.getPrecision(), p.getScale());
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_DECIMAL_PRECISION;
    }
}
