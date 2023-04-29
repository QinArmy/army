package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PgSqlType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class IntegerArrayType extends _ArmyNoInjectionMapping
        implements MappingType.SqlIntegerType,
        MappingType.SqlArrayType {

    public static IntegerArrayType from(final Class<?> javaType) {
        if (!javaType.isArray()) {
            throw errorJavaType(IntegerArrayType.class, javaType);
        }

        return INSTANCE_MAP.computeIfAbsent(javaType, IntegerArrayType::new);
    }

    public static final IntegerArrayType UNLIMITED = new IntegerArrayType();

    private static final ConcurrentMap<Class<?>, IntegerArrayType> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Class<?> javaType;

    private final Class<?> underlyingType;

    /**
     * @see #UNLIMITED
     */
    private IntegerArrayType() {
        this.javaType = Object.class;
        this.underlyingType = Object.class;
    }

    private IntegerArrayType(Class<?> javaType) {
        this.javaType = javaType;
        this.underlyingType = _ArrayUtils.underlyingComponent(javaType);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PgSqlType.VARCHAR_ARRAY;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        if (nonNull instanceof String) {
            return nonNull;
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }


}
