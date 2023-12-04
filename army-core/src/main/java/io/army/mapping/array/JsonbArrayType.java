package io.army.mapping.array;

import io.army.codec.JsonCodec;
import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.function.TextFunction;
import io.army.mapping.JsonbType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
                dataType = PostgreType.JSONB_ARRAY;
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
        return decodeJson(map(env.serverMeta()), env, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        final JsonCodec codec;
        codec = env.jsonCodec();

        final BiConsumer<Object, Consumer<String>> consumer;
        consumer = (element, appender) -> {
            if (!this.underlyingJavaType.isInstance(element)) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
            appender.accept(codec.encode(element)); // TODO escapes json
            appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
        };

        return PostgreArrays.arrayBeforeBind(source, consumer, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return decodeJson(dataType, env, source, ACCESS_ERROR_HANDLER);
    }

    private Object decodeJson(DataType dataType, MappingEnv env, final Object source, ErrorHandler errorHandler) {
        final JsonCodec codec;
        codec = env.jsonCodec();

        final TextFunction<?> function;
        function = (text, offset, end) -> codec.decode(text.substring(offset, end), this.underlyingJavaType);

        return PostgreArrays.arrayAfterGet(this, dataType, source, false, function, errorHandler);
    }


}
