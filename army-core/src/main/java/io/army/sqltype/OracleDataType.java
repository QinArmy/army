package io.army.sqltype;

import io.army.Database;

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


}
