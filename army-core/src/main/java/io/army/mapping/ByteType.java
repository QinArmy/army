package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlDataType;

public final class ByteType extends _ArmyNoInjectionMapping {

    public static final ByteType INSTANCE = new ByteType();

    private ByteType() {
    }

    @Override
    public Class<?> javaType() {
        return Byte.class;
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
