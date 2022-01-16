package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;


public final class FloatType extends _ArmyNoInjectionMapping {

    public static final FloatType INSTANCE = new FloatType();

    private FloatType() {
    }

    @Override
    public Class<?> javaType() {
        return Float.class;
    }


    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        return null;
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        return null;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
        return null;
    }


}
