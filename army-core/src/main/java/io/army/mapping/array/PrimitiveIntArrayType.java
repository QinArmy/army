package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Deprecated
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
        return IntegerType.INTEGER;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws UnsupportedDialectException {
        return IntegerArrayType.mapSqlType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType type, MappingEnv env, Object source) throws CriteriaException {
        if (source instanceof String
                || source instanceof int[]
                || ArrayUtils.underlyingComponent(source.getClass()) == int.class) {
            return source;
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
