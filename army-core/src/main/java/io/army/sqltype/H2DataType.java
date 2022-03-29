package io.army.sqltype;

import io.army.session.Database;

public enum H2DataType implements SqlType {


    BOOLEAN,
    DECIMAL,
    VARBINARY,
    ENUM;


    @Override
    public final Database database() {
        return Database.H2;
    }


}
