package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;


public interface MappingType extends ParamMeta {

    Class<?> javaType();

    SqlType map(ServerMeta meta);

    Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull);

    Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull);


    default Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        throw new UnsupportedOperationException();
    }


}
