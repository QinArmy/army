package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class StringArrayType extends _ArmyBuildInMapping {

    public static StringArrayType from(final Class<?> javaType) {
        if (!javaType.isArray()) {
            throw errorJavaType(StringArrayType.class, javaType);
        }

        return INSTANCE_MAP.computeIfAbsent(javaType, StringArrayType::new);
    }

    public static final StringArrayType UNLIMITED = new StringArrayType();

    public static final StringArrayType LINEAR = new StringArrayType(String[].class);

    private static final ConcurrentMap<Class<?>, StringArrayType> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Class<?> javaType;

    private final Class<?> underlyingType;

    /**
     * @see #UNLIMITED
     */
    private StringArrayType() {
        this.javaType = Object.class;
        this.underlyingType = Object.class;
    }

    private StringArrayType(Class<?> javaType) {
        this.javaType = javaType;
        this.underlyingType = ArrayUtils.underlyingComponent(javaType);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.VARCHAR_ARRAY;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
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
