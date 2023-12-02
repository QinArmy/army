package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.SqlType;
import io.army.struct.TextEnum;
import io.army.util._Collections;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * This class representing the mapping from {@link TextEnum} to {@link SqlType}.
 * </p>
 *
 * @see TextEnum
 * @see NameEnumType
 * @see CodeEnumType
 */
public final class TextEnumType extends MappingType {

    public static TextEnumType from(final Class<?> fieldType) {
        if (!Enum.class.isAssignableFrom(fieldType) || !TextEnum.class.isAssignableFrom(fieldType)) {
            throw errorJavaType(TextEnumType.class, fieldType);
        }
        final Class<?> actualType;
        if (fieldType.isAnonymousClass()) {
            actualType = fieldType.getSuperclass();
        } else {
            actualType = fieldType;
        }
        return INSTANCE_MAP.computeIfAbsent(actualType, TextEnumType::new);
    }

    private static final ConcurrentMap<Class<?>, TextEnumType> INSTANCE_MAP = _Collections.concurrentHashMap();


    private final Class<?> javaType;

    private final Map<String, ? extends TextEnum> textMap;

    /**
     * private constructor
     */
    private TextEnumType(final Class<?> javaType) {
        this.javaType = javaType;
        this.textMap = TextEnum.getTextToEnumMap(javaType);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return NameEnumType.mapToSqlType(this, meta);
    }


    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public TextEnum convert(MappingEnv env, Object source) throws CriteriaException {
        return toTextEnum(map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toTextEnum(dataType, source, PARAM_ERROR_HANDLER).text();
    }

    @Override
    public TextEnum afterGet(DataType dataType, MappingEnv env, Object source) {
        return toTextEnum(dataType, source, ACCESS_ERROR_HANDLER);
    }

    private TextEnum toTextEnum(final DataType dataType, final Object nonNull, final ErrorHandler errorHandler) {
        final TextEnum value;
        if (this.javaType.isInstance(nonNull)) {
            value = ((TextEnum) nonNull);
        } else if (!(nonNull instanceof String)) {
            throw errorHandler.apply(this, dataType, nonNull, null);
        } else if ((value = this.textMap.get(nonNull)) == null) {
            throw errorHandler.apply(this, dataType, nonNull, null);
        }
        return value;
    }

}
