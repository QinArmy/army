package io.army.sqltype;

import io.army.dialect.Database;

import javax.annotation.Nullable;

public enum H2DataType implements SqlType {


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
    public ArmyType armyType() {
        return null;
    }

    @Override
    public Class<?> firstJavaType() {
        return null;
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
