package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.math.BigDecimal;

public final class BooleanArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static BooleanArrayType from(final Class<?> javaType) {
        final BooleanArrayType instance;

        final Class<?> underlyingJavaType;
        if (javaType == Boolean[].class) {
            instance = LINEAR;
        } else if (javaType == boolean[].class) {
            instance = PRIMITIVE_UNLIMITED;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(BooleanArrayType.class, javaType);
        } else if ((underlyingJavaType = ArrayUtils.underlyingComponent(javaType)) == Boolean.class) {
            instance = new BooleanArrayType(javaType, Boolean.class);
        } else if (underlyingJavaType == boolean.class) {
            instance = new BooleanArrayType(javaType, boolean.class);
        } else {
            throw errorJavaType(BooleanArrayType.class, javaType);
        }
        return instance;
    }

    /**
     * unlimited dimension array of {@code boolean}
     */
    public static final BooleanArrayType PRIMITIVE_UNLIMITED = new BooleanArrayType(Object.class, boolean.class);

    /**
     * one dimension array of {@code  boolean}
     */
    public static final BooleanArrayType PRIMITIVE_LINEAR = new BooleanArrayType(boolean[].class, boolean.class);

    /**
     * one dimension array of {@link Boolean}
     */
    public static final BooleanArrayType LINEAR = new BooleanArrayType(Boolean[].class, Boolean.class);

    /**
     * unlimited dimension array of {@link Boolean}
     */
    public static final BooleanArrayType UNLIMITED = new BooleanArrayType(Object.class, Boolean.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;

    /**
     * private constructor
     */
    private BooleanArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
        this.javaType = javaType;
        this.underlyingJavaType = underlyingJavaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingElementJavaType() {
        return this.underlyingJavaType;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.BOOLEAN_ARRAY;
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
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == Boolean[].class || javaType == boolean[].class) {
            instance = BooleanType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return null;
    }


}
