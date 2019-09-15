package io.army.meta.sqltype.mysql;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class MySQLTime extends MySQLDataType {

    public static final MySQLTime INSTANCE = new MySQLTime();

    private MySQLTime() {

    }

    @Override
    public DataKind dataKind() {
        return DataKind.TIME;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        if (scale > 6) {
            throw new MetaException(ErrorCode.META_ERROR, "MySQL TIME precision[%s] must cannot great than 6");
        }
        return String.format("TIME(%s)", precision);
    }

    @Override
    protected String innerTypeName(int precision) {
        return innerTypeName(precision, 0);
    }

    @Override
    protected String innerTypeName() {
        return "TIME";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }
}
