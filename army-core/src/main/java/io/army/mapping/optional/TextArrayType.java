package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyInnerMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class TextArrayType extends _ArmyInnerMapping {


    public static TextArrayType from(final Class<?> javaType) {
        if (!javaType.isArray()) {
            throw errorJavaType(StringArrayType.class, javaType);
        }

        return INSTANCE_MAP.computeIfAbsent(javaType, TextArrayType::new);
    }


    public static final TextArrayType UNLIMITED = new TextArrayType();

    private static final ConcurrentMap<Class<?>, TextArrayType> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Class<?> javaType;

    private final Class<?> underlyingType;


    /**
     * @see #UNLIMITED
     */
    private TextArrayType() {
        this.javaType = Object.class;
        this.underlyingType = Object.class;
    }


    private TextArrayType(Class<?> javaType) {
        this.javaType = javaType;
        this.underlyingType = _ArrayUtils.underlyingComponent(javaType);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.database() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreTypes.TEXT_ARRAY;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }


}
