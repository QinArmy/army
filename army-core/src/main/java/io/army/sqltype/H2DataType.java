package io.army.sqltype;

import io.army.dialect.Database;

public enum H2DataType implements SQLType {


    BOOLEAN,
    DECIMAL,
    VARBINARY,
    ENUM,
    ;


    @Override
    public final Database database() {
        return Database.H2;
    }

    @Override
    public final boolean isUserDefined() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUnknown() {
        throw new UnsupportedOperationException();
    }


}
