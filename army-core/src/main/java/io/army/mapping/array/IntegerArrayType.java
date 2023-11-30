package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.List;

public class IntegerArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static IntegerArrayType from(final Class<?> javaType) {
        final IntegerArrayType instance;
        final Class<?> componentType;
        if (javaType == Integer[].class) {
            instance = LINEAR;
        } else if (javaType == int[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(IntegerArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == int.class
                || componentType == Integer.class) {
            instance = new IntegerArrayType(javaType);
        } else {
            throw errorJavaType(IntegerArrayType.class, javaType);
        }
        return instance;
    }

    public static final IntegerArrayType UNLIMITED = new IntegerArrayType(Object.class);

    public static final IntegerArrayType LINEAR = new IntegerArrayType(Integer[].class);

    public static final IntegerArrayType PRIMITIVE_LINEAR = new IntegerArrayType(int[].class);


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
    public final MappingType elementType() {
        return null;
    }


    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        return mapSqlType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        if (source instanceof String || source instanceof int[] || source instanceof Integer[]) {
            return source;
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    static SqlType mapSqlType(final MappingType mappingType, final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                type = PostgreType.INTEGER_ARRAY;
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
