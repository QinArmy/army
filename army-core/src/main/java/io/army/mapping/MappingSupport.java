package io.army.mapping;

import io.army.ArmyException;
import io.army.sqltype.SqlType;

import javax.annotation.Nullable;

public abstract class MappingSupport {

    protected MappingSupport() {

    }


    protected interface ErrorHandler {

        ArmyException apply(MappingType type, SqlType sqlType, Object value, @Nullable Throwable e);

    }


}
