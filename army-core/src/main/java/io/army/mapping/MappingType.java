package io.army.mapping;

import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;


public interface MappingType extends ParamMeta {

    Class<?> javaType();

    SqlType map(ServerMeta meta);

    Object beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull);

    Object afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull);

}
