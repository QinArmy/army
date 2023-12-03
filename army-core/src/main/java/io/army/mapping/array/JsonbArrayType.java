package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.JsonbType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

public final class JsonbArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static JsonbArrayType from(final Class<?> arrayClass) {
        final JsonbArrayType instance;
        if (!arrayClass.isArray()) {
            throw errorJavaType(JsonbArrayType.class, arrayClass);
        } else if (arrayClass == String[].class) {
            instance = TEXT_LINEAR;
        } else {
            instance = new JsonbArrayType(arrayClass, ArrayUtils.underlyingComponent(arrayClass));
        }
        return instance;
    }

    public static JsonbArrayType fromUnlimited(final Class<?> underlyingJavaType) {
        final JsonbArrayType instance;
        if (underlyingJavaType == String.class) {
            instance = TEXT_UNLIMITED;
        } else if (underlyingJavaType.isArray()) {
            throw errorJavaType(JsonbArrayType.class, underlyingJavaType);
        } else {
            instance = new JsonbArrayType(Object.class, underlyingJavaType);
        }
        return instance;
    }

    public static final JsonbArrayType TEXT_LINEAR = new JsonbArrayType(String[].class, String.class);

    public static final JsonbArrayType TEXT_UNLIMITED = new JsonbArrayType(Object.class, String.class);

    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;

    /**
     * private constructor
     */
    private JsonbArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
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
        if (javaType == Object.class) {
            instance = this;
        } else if (ArrayUtils.dimensionOf(javaType) == 1) {
            instance = JsonbType.from(this.underlyingJavaType);
        } else {
            instance = from(this.underlyingJavaType);
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return super.arrayTypeOfThis();
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return null;
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
