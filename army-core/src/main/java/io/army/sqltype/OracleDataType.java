package io.army.sqltype;

import io.army.dialect.Database;

public enum OracleDataType implements SqlType {

    NUMBER,
    CHAR,
    VARCHAR2,
    NVARCHAR2,
    BLOB,
    TIMESTAMPTZ;


    @Override
    public final Database database() {
        return Database.Oracle;
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
