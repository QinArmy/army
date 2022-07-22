package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;
import io.army.struct.TextEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see TextEnum
 * @see NameEnumType
 * @see CodeEnumType
 */
public final class TextEnumType extends AbstractMappingType {

    private static final ConcurrentMap<Class<?>, TextEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static TextEnumType from(final Class<?> fieldType) {
        if (!fieldType.isEnum() || !TextEnum.class.isAssignableFrom(fieldType)) {
            throw errorJavaType(TextEnumType.class, fieldType);
        }
        return INSTANCE_MAP.computeIfAbsent(fieldType, TextEnumType::new);
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
        final SqlType sqlType;
        sqlType = NameEnumType.mapToSqlType(meta);
        if (sqlType == null) {
            throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!this.javaType.isInstance(nonNull)) {
            String m = String.format("%s isn't %s type.", nonNull.getClass().getName(), this.javaType.getName());
            throw outRangeOfSqlType(sqlType, nonNull, new CriteriaException(m));
        }
        return ((TextEnum) nonNull).text();
    }

    @Override
    public TextEnum afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final TextEnum value;
        value = this.textMap.get(nonNull);
        if (value == null) {
            String m = String.format("%s don't contain text[%s]", this.javaType.getName(), nonNull);
            throw errorValueForSqlType(sqlType, nonNull, new IllegalArgumentException(m));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & TextEnum> Map<String, T> getTextMap(Class<?> javaType) {
        return TextEnum.getInstanceMap((Class<T>) javaType);
    }


}
