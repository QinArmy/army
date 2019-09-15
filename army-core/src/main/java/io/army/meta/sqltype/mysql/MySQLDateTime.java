package io.army.meta.sqltype.mysql;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.meta.sqltype.DataKind;
import io.army.util.Precision;

public final class MySQLDateTime extends MySQLDataType {

    public static final MySQLDateTime INSTANCE = new MySQLDateTime();

    private MySQLDateTime() {

    }

    @Override
    public DataKind dataKind() {
        return DataKind.DATE_TIME;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        if (scale > 6) {
            throw new MetaException(ErrorCode.META_ERROR, "MySQL DATETIME precision[%s] must cannot great than 6");
        }
        return innerTypeName(precision);
    }

    @Override
    protected String innerTypeName(int precision) {
        return String.format("DATETIME(%s)", precision);
    }

    @Override
    protected String innerTypeName() {
        return innerTypeName(0);
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }

}
