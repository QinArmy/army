package io.army.mapping;

import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;


public interface MappingType extends ParamMeta {

    Class<?> javaType();

    default Class<?> elementType() {
        return void.class;
    }

    SqlType map(ServerMeta meta);

    default Object beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    default Object afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default Object beforeBind_(SqlType sqlType, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default Object afterGet_(SqlType sqlType, Object nonNull) {
        throw new UnsupportedOperationException();
    }

}
