package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;
import io.army.util.ArrayUtils;

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
                && ArrayUtils.underlyingComponent(javaType) != Integer.class) {
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
    public SQLType map(final ServerMeta meta) throws NotSupportDialectException {
        return mapSqlType(this, meta);
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
    public Object beforeBind(SQLType type, MappingEnv env, Object nonNull) throws CriteriaException {
        if (nonNull instanceof String || nonNull instanceof int[] || nonNull instanceof Integer[]) {
            return nonNull;
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SQLType type, MappingEnv env, Object nonNull) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    static SQLType mapSqlType(final MappingType mappingType, final ServerMeta meta) {
        final SQLType type;
        switch (meta.dialectDatabase()) {
            case PostgreSQL:
                type = PostgreSqlType.INTEGER_ARRAY;
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
