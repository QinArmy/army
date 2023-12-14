package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.NameEnumArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.H2DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see TextEnumType
 */
public final class NameEnumType extends _ArmyNoInjectionMapping {

    public static NameEnumType from(final Class<?> enumType) {
        final Class<?> actualEnumType;
        actualEnumType = checkEnumClass(enumType);
        return INSTANCE_MAP.computeIfAbsent(actualEnumType, k -> new NameEnumType(actualEnumType, null));
    }

    public static NameEnumType fromParam(final Class<?> enumType, final String enumName) {
        if (!_StringUtils.hasText(enumName)) {
            throw new IllegalArgumentException("no text");
        }
        final Class<?> actualEnumType;
        actualEnumType = checkEnumClass(enumType);

        final String key;
        key = actualEnumType.getName() + '#' + enumName;
        return INSTANCE_MAP.computeIfAbsent(key, k -> new NameEnumType(actualEnumType, enumName));
    }

    private static Class<?> checkEnumClass(final Class<?> javaType) {
        if (!Enum.class.isAssignableFrom(javaType)) {
            throw errorJavaType(NameEnumType.class, javaType);
        }
        if (CodeEnum.class.isAssignableFrom(javaType)) {
            String m = String.format("enum %s implements %s,please use %s.", javaType.getName(),
                    CodeEnum.class.getName(), CodeEnumType.class.getName());
            throw new IllegalArgumentException(m);
        }
        if (TextEnum.class.isAssignableFrom(javaType)) {
            String m = String.format("enum %s implements %s,please use %s.", javaType.getName(),
                    TextEnum.class.getName(), TextEnumType.class.getName());
            throw new IllegalArgumentException(m);
        }
        return ClassUtils.enumClass(javaType);
    }

    private static final ConcurrentMap<Object, NameEnumType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private final Class<?> enumClass;

    private final String enumName;

    /**
     * private constructor
     */
    private NameEnumType(Class<?> enumClass, @Nullable String enumName) {
        this.enumClass = enumClass;
        this.enumName = enumName;
    }


    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return NameEnumArrayType.from(this.enumClass);
    }

    @Override
    public boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (type instanceof NameEnumType) {
            final NameEnumType o = (NameEnumType) type;
            match = o.enumClass == this.enumClass && Objects.equals(o.enumName, this.enumName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToDataType(this, meta, this.enumName);
    }


    @Override
    public Enum<?> convert(MappingEnv env, Object source) throws CriteriaException {
        return toNameEnum(map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toNameEnum(dataType, source, PARAM_ERROR_HANDLER).name();
    }

    @Override
    public Enum<?> afterGet(DataType dataType, MappingEnv env, Object source) {
        return toNameEnum(dataType, source, ACCESS_ERROR_HANDLER);
    }


    private Enum<?> toNameEnum(final DataType dataType, final Object nonNull, final ErrorHandler errorHandler) {
        final Enum<?> value;
        if (nonNull instanceof String) {
            try {
                value = valueOf(this.enumClass, (String) nonNull);
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(this, dataType, nonNull, e);
            }
        } else if (this.enumClass.isInstance(nonNull)) {
            value = (Enum<?>) nonNull;
        } else {
            throw errorHandler.apply(this, dataType, nonNull, null);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T valueOf(final Class<?> javaType, final String name)
            throws IllegalArgumentException {
        if (!Enum.class.isAssignableFrom(javaType)) {
            throw new IllegalArgumentException("not enum type.");
        }
        return Enum.valueOf((Class<T>) javaType, name);
    }


    static DataType mapToDataType(final MappingType type, final ServerMeta meta, final @Nullable String enumName) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.ENUM;
                break;
            case PostgreSQL: {
                if (enumName == null) {
                    dataType = PostgreType.VARCHAR;
                } else {
                    dataType = DataType.from(enumName);
                }
            }
            break;
            case H2:
                dataType = H2DataType.ENUM;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return dataType;
    }


}
