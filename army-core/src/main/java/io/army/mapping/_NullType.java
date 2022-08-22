package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;

public final class _NullType extends AbstractMappingType {

    public static final _NullType INSTANCE = new _NullType();


    private _NullType() {
    }

    @Override
    public Class<?> javaType() {
        return Object.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.NULL;
                break;
            case PostgreSQL:
            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }


}
