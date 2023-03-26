package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.TypeInfer;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.SqlType;


public interface MappingType extends TypeMeta, TypeInfer {

    /**
     * @return always return this
     */
    @Override
    TypeMeta typeMeta();


    /**
     * @return always return this
     */
    @Override
    MappingType mappingType();

    Class<?> javaType();

    SqlType map(ServerMeta meta);

    Object beforeBind(SqlType type, MappingEnv env, Object nonNull);

    Object afterGet(SqlType type, MappingEnv env, Object nonNull);


    default Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        throw new UnsupportedOperationException();
    }


}
