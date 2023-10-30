package io.army.mapping;

import io.army.ArmyException;

import javax.annotation.Nullable;

import io.army.sqltype.SqlType;

public abstract class MappingSupport {

    protected MappingSupport() {

    }


    protected interface ErrorHandler {

        ArmyException apply(MappingType type, SqlType sqlType, Object value, @Nullable Throwable e);

    }


}
