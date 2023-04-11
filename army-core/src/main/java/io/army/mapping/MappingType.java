package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.TypeInfer;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.session.DataAccessException;
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

    SqlType map(ServerMeta meta) throws NotSupportDialectException;

    /**
     * @return the instance of {@link #javaType()}.
     */
    Object convert(MappingEnv env, Object nonNull) throws CriteriaException;


    /**
     * @param type from {@link #map(ServerMeta)}
     * @return the instance of the type that {@link SqlType} allow.
     */
    Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException;

    /**
     * @param type from {@code io.army.sync.executor.StmtExecutor} or {@code io.army.reactive.executor.StmtExecutor}
     * @return the instance of {@link #javaType()}.
     */
    Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException;




}
