package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

public class DoubleArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static DoubleArrayType from(final Class<?> javaType) {
        final DoubleArrayType instance;
        final Class<?> componentType;
        if (javaType == Double[].class) {
            instance = LINEAR;
        } else if (javaType == double[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(DoubleArrayType.class, javaType);
        } else if ((componentType = ArrayUtils.underlyingComponent(javaType)) == double.class
                || componentType == Double.class) {
            instance = new DoubleArrayType(javaType);
        } else {
            throw errorJavaType(DoubleArrayType.class, javaType);
        }
        return instance;
    }


    public static final DoubleArrayType UNLIMITED = new DoubleArrayType(Object.class);

    public static final DoubleArrayType LINEAR = new DoubleArrayType(Double[].class);

    public static final DoubleArrayType PRIMITIVE_LINEAR = new DoubleArrayType(double[].class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private DoubleArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == double[].class || javaType == Double[].class) {
            instance = DoubleType.INSTANCE;
        } else {
            instance = from(this.javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        return from(ArrayUtils.arrayClassOf(this.javaType));
    }

    @Override
    public final DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.FLOAT8_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public final <Z> MappingType compatibleFor(final Class<Z> targetType) throws NoMatchMappingException {
        if (targetType != String.class) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return StringType.INSTANCE;
    }


    @Override
    public final Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public final Object beforeBind(DataType dataType, MappingEnv env, Object nonNull) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, Object nonNull) throws DataAccessException {
        throw new UnsupportedOperationException();
    }


}
