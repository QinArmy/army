package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.FloatType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;

public final class FloatArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static FloatArrayType from(final Class<?> arrayClass) {
        final FloatArrayType instance;
        final Class<?> componentType;
        if (arrayClass == Float[].class) {
            instance = LINEAR;
        } else if (arrayClass == float[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(FloatArrayType.class, arrayClass);
        } else if ((componentType = ArrayUtils.underlyingComponent(arrayClass)) == float.class
                || componentType == Float.class) {
            instance = new FloatArrayType(arrayClass, componentType);
        } else {
            throw errorJavaType(FloatArrayType.class, arrayClass);
        }
        return instance;
    }

    public static FloatArrayType fromUnlimited(final Class<?> floatClass) {
        final FloatArrayType instance;
        if (floatClass == Float.class) {
            instance = UNLIMITED;
        } else if (floatClass == float.class) {
            instance = PRIMITIVE_UNLIMITED;
        } else {
            throw errorJavaType(FloatArrayType.class, floatClass);
        }
        return instance;
    }


    public static final FloatArrayType UNLIMITED = new FloatArrayType(Object.class, Float.class);

    public static final FloatArrayType LINEAR = new FloatArrayType(Float[].class, Float.class);

    public static final FloatArrayType PRIMITIVE_LINEAR = new FloatArrayType(float[].class, float.class);

    public static final FloatArrayType PRIMITIVE_UNLIMITED = new FloatArrayType(Object.class, float.class);

    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;

    /**
     * private constructor
     */
    private FloatArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
        this.javaType = javaType;
        this.underlyingJavaType = underlyingJavaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return this.underlyingJavaType;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == float[].class || javaType == Float[].class) {
            instance = FloatType.INSTANCE;
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
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.REAL_ARRAY;
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
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, FloatArrayType::parseText, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, FloatArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, FloatArrayType::parseText, ACCESS_ERROR_HANDLER);
    }


    /*-------------------below static methods -------------------*/

    private static float parseText(final String text, final int offset, final int end) {
        return Float.parseFloat(text.substring(offset, end));
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof Float)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(element.toString());
    }


}
