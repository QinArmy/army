package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PgSqlType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class IntegerArrayType extends _ArmyNoInjectionMapping
        implements MappingType.SqlIntegerType,
        MappingType.SqlArrayType {

    public static IntegerArrayType from(final Class<?> javaType) {
        final IntegerArrayType instance;
        if (List.class.isAssignableFrom(javaType)) {
            instance = LIST;
        } else if (javaType == Integer[].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (javaType.isArray()) {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, IntegerArrayType::new);
        } else {
            throw errorJavaType(IntegerArrayType.class, javaType);
        }
        return instance;
    }

    public static final IntegerArrayType UNLIMITED = new IntegerArrayType(Object.class);
    public static final IntegerArrayType LINEAR = new IntegerArrayType(Integer[].class);

    public static final IntegerArrayType LIST = new IntegerArrayType(List.class);

    private static final ConcurrentMap<Class<?>, IntegerArrayType> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Class<?> javaType;


    private IntegerArrayType(final Class<?> javaType) {
        this.javaType = javaType;
        if (javaType != Object.class
                && !List.class.isAssignableFrom(javaType)
                && _ArrayUtils.underlyingComponent(javaType) != Integer.class) {
            throw errorJavaType(IntegerArrayType.class, javaType);
        }
    }


    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingComponentType() {
        return Integer.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        return mapSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        if (nonNull instanceof String || nonNull instanceof int[] || nonNull instanceof Integer[]) {
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

    static SqlType mapSqlType(final MappingType mappingType, final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case PostgreSQL:
                type = PgSqlType.INTEGER_ARRAY;
                break;
            case Oracle:
            case H2:
            case MySQL:
            default:
                throw MAP_ERROR_HANDLER.apply(mappingType, meta);
        }
        return type;
    }


}