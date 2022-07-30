package io.army.sqltype;

import io.army.dialect.Database;

public enum H2DataType implements SqlType {


    BOOLEAN,
    DECIMAL,
    VARBINARY,
    ENUM;


    @Override
    public final Database database() {
        throw new UnsupportedOperationException();
    }


}
