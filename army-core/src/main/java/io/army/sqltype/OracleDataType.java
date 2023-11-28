package io.army.sqltype;

import io.army.dialect.Database;

import javax.annotation.Nullable;

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
    public ArmyType armyType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> firstJavaType() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Class<?> secondJavaType() {
        return null;
    }

    @Nullable
    @Override
    public SqlType elementType() {
        return null;
    }


    @Override
    public String typeName() {
        return null;
    }

    @Override
    public boolean isUnknown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArray() {
        return false;
    }


}
