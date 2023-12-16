package io.army.mapping.array;


import io.army.codec.JsonCodec;
import io.army.criteria.CriteriaException;
import io.army.function.TextFunction;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * <p>Package class
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link JsonArrayType}</li>
 *     <li>{@link JsonbArrayType}</li>
 * </ul>
 *
 * @since 0.6.0
 */
abstract class ArmyJsonArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {


    final Class<?> javaType;

    final Class<?> underlyingJavaType;

    /**
     * package constructor
     */
    ArmyJsonArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
        this.javaType = javaType;
        this.underlyingJavaType = underlyingJavaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final Class<?> underlyingJavaType() {
        return this.underlyingJavaType;
    }


    @Override
    public final Object convert(MappingEnv env, Object source) throws CriteriaException {
        return decodeJsonArray(map(env.serverMeta()), env, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        final JsonCodec codec;
        codec = env.jsonCodec();

        final BiConsumer<Object, Consumer<String>> consumer;
        consumer = (element, appender) -> {
            if (!this.underlyingJavaType.isInstance(element)) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            PostgreArrays.encodeElement(codec.encode(element), appender);
        };

        return PostgreArrays.arrayBeforeBind(source, consumer, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return decodeJsonArray(dataType, env, source, ACCESS_ERROR_HANDLER);
    }

    private Object decodeJsonArray(DataType dataType, MappingEnv env, final Object source, ErrorHandler errorHandler) {
        final JsonCodec codec;
        codec = env.jsonCodec();

        final TextFunction<?> function;
        function = (text, offset, end) -> {
            final String json;
            json = PostgreArrays.decodeElement(text, offset, end);
            return codec.decode(json, this.underlyingJavaType);
        };

        return PostgreArrays.arrayAfterGet(this, dataType, source, false, function, errorHandler);
    }


}
