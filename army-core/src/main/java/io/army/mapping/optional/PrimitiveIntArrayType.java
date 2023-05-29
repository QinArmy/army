package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class PrimitiveIntArrayType extends _ArmyNoInjectionMapping
        implements MappingType.SqlIntegerType,
        MappingType.SqlArrayType {


    public static PrimitiveIntArrayType from(final Class<?> javaType) {
        final PrimitiveIntArrayType instance;
        if (List.class.isAssignableFrom(javaType)) {
            instance = LIST;
        } else if (javaType == Integer[].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (javaType.isArray()) {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, PrimitiveIntArrayType::new);
        } else {
            throw errorJavaType(PrimitiveIntArrayType.class, javaType);
        }
        return instance;
    }

    public static final PrimitiveIntArrayType UNLIMITED = new PrimitiveIntArrayType(Object.class);
    public static final PrimitiveIntArrayType LINEAR = new PrimitiveIntArrayType(int[].class);

    public static final PrimitiveIntArrayType LIST = new PrimitiveIntArrayType(List.class);

    private static final ConcurrentMap<Class<?>, PrimitiveIntArrayType> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Class<?> javaType;

    private PrimitiveIntArrayType(final Class<?> javaType) {
        this.javaType = javaType;
        if (javaType != Object.class
                && !List.class.isAssignableFrom(javaType)
                && ArrayUtils.underlyingComponent(javaType) != int.class) {
            throw errorJavaType(IntegerArrayType.class, javaType);
        }
    }


    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public MappingType elementType() {
        return IntegerType.INSTANCE;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        return IntegerArrayType.mapSqlType(this, meta);
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
        if (nonNull instanceof String
                || nonNull instanceof int[]
                || ArrayUtils.underlyingComponent(nonNull.getClass()) == int.class) {
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
