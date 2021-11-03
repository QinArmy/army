package io.army.sqltype;

import io.army.dialect.Database;

public enum H2DataType implements SqlDataType {


    BOOLEAN,
    DECIMAL,
    VARBINARY,
    ENUM;


    @Override
    public final Database database() {
        return Database.H2;
    }


}
