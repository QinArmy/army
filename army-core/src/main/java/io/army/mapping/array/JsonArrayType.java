package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.JsonType;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

public final class JsonArrayType extends ArmyJsonArrayType {

    public static JsonArrayType from(final Class<?> arrayClass) {
        final JsonArrayType instance;
        if (!arrayClass.isArray()) {
            throw errorJavaType(JsonArrayType.class, arrayClass);
        } else if (arrayClass == String[].class) {
            instance = TEXT_LINEAR;
        } else {
            instance = new JsonArrayType(arrayClass, ArrayUtils.underlyingComponent(arrayClass));
        }
        return instance;
    }

    public static JsonArrayType fromUnlimited(final Class<?> underlyingJavaType) {
        final JsonArrayType instance;
        if (underlyingJavaType == String.class) {
            instance = TEXT_UNLIMITED;
        } else if (underlyingJavaType.isArray()) {
            throw errorJavaType(JsonArrayType.class, underlyingJavaType);
        } else {
            instance = new JsonArrayType(Object.class, underlyingJavaType);
        }
        return instance;
    }

    public static final JsonArrayType TEXT_LINEAR = new JsonArrayType(String[].class, String.class);

    public static final JsonArrayType TEXT_UNLIMITED = new JsonArrayType(Object.class, String.class);

    /**
     * private constructor
     */
    private JsonArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
        super(javaType, underlyingJavaType);
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (ArrayUtils.dimensionOf(javaType) == 1) {
            instance = JsonType.from(this.underlyingJavaType);
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
                dataType = PostgreType.JSON_ARRAY;
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


}
