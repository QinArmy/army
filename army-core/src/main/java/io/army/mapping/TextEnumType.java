package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util._ClassUtils;
import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * This class representing the mapping from {@link TextEnum} to {@link SqlType}.
*
 * @see TextEnum
 * @see NameEnumType
 * @see CodeEnumType
 */
public final class TextEnumType extends MappingType {

    public static TextEnumType from(final Class<?> enumType) {
        if (!Enum.class.isAssignableFrom(enumType)
                || !TextEnum.class.isAssignableFrom(enumType)
                || CodeEnum.class.isAssignableFrom(enumType)) {
            throw errorJavaType(TextEnumType.class, enumType);
        }
        final Class<?> actualType;
        if (enumType.isAnonymousClass()) {
            actualType = enumType.getSuperclass();
        } else {
            actualType = enumType;
        }
        return INSTANCE_MAP.computeIfAbsent(actualType, k -> new TextEnumType(actualType, null));
    }

    public static TextEnumType fromParam(final Class<?> enumType, final String enumName) {
        if (!Enum.class.isAssignableFrom(enumType)
                || !TextEnum.class.isAssignableFrom(enumType)
                || CodeEnum.class.isAssignableFrom(enumType)) {
            throw errorJavaType(TextEnumType.class, enumType);
        } else if (!_StringUtils.hasText(enumName)) {
            throw new IllegalArgumentException("no text");
        }
        final Class<?> actualEnumType;
        actualEnumType = _ClassUtils.enumClass(enumType);

        final String key;
        key = actualEnumType.getName() + '#' + enumName;
        return INSTANCE_MAP.computeIfAbsent(key, k -> new TextEnumType(actualEnumType, enumName));
    }

    private static final ConcurrentMap<Object, TextEnumType> INSTANCE_MAP = _Collections.concurrentHashMap();


    private final Class<?> enumClass;

    private final String enumName;

    private final Map<String, ? extends TextEnum> textMap;

    /**
     * private constructor
     */
    private TextEnumType(final Class<?> enumClass, @Nullable String enumName) {
        this.enumClass = enumClass;
        this.enumName = enumName;
        this.textMap = TextEnum.getTextToEnumMap(enumClass);
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (type instanceof TextEnumType) {
            final TextEnumType o = (TextEnumType) type;
            match = o.enumClass == this.enumClass && Objects.equals(o.enumName, this.enumName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return NameEnumType.mapToDataType(this, meta, this.enumName);
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
        if (this.enumClass.isInstance(nonNull)) {
            value = ((TextEnum) nonNull);
        } else if (!(nonNull instanceof String)) {
            throw errorHandler.apply(this, dataType, nonNull, null);
        } else if ((value = this.textMap.get(nonNull)) == null) {
            throw errorHandler.apply(this, dataType, nonNull, null);
        }
        return value;
    }

}
