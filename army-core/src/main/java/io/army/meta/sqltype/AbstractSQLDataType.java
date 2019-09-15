package io.army.meta.sqltype;

import io.army.util.Precision;

public abstract class AbstractSQLDataType implements SQLDataType {


    @Override
    public final String typeName(int precision, int scale) {
        Precision p = defaultPrecision();
        int actualPrecision = p.getPrecision(), actualScale = p.getScale();
        if (precision < 0) {
            actualPrecision = precision;
        }
        if (scale < 0) {
            actualScale = scale;
        }

        String name;
        if (actualPrecision >= 0) {
            if (actualScale >= 0) {
                name = innerTypeName(precision, scale);
            } else {
                name = innerTypeName(precision);
            }
        } else {
            name = innerTypeName();
        }
        return name;
    }


    /*########################## template method ##############################*/

    protected abstract String innerTypeName(int precision, int scale);

    protected abstract String innerTypeName(int precision);

    protected abstract String innerTypeName();

    protected abstract Precision defaultPrecision();
}
