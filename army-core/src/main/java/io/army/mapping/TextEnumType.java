package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.struct.TextEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private static final ConcurrentMap<Class<?>, TextEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();

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


    private final Class<?> javaType;

    private final Map<String, ? extends TextEnum> textMap;

    private TextEnumType(final Class<?> javaType) {
        this.javaType = javaType;
        this.textMap = getTextMap(javaType);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        return NameEnumType.mapToSqlEnumType(this, meta);
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public TextEnum convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!this.javaType.isInstance(nonNull)) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (TextEnum) nonNull;
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        if (!this.javaType.isInstance(nonNull)) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return ((TextEnum) nonNull).text();
    }

    @Override
    public TextEnum afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        final TextEnum value;
        value = this.textMap.get(nonNull);
        if (value == null) {
            String m = String.format("%s don't contain text[%s]", this.javaType.getName(), nonNull);
            throw new DataAccessException(m);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & TextEnum> Map<String, T> getTextMap(Class<?> javaType) {
        return TextEnum.getInstanceMap((Class<T>) javaType);
    }


}
