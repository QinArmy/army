package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlDataType;


public final class FloatType extends _ArmyNoInjectionMapping {

    public static final FloatType INSTANCE = new FloatType();

    private FloatType() {
    }

    @Override
    public Class<?> javaType() {
        return Float.class;
    }


    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        return null;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, Object nonNull) {
        return null;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        return null;
    }


}
