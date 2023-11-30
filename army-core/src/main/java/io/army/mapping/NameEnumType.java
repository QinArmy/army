package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see TextEnumType
 */
public final class NameEnumType extends _ArmyNoInjectionMapping {

    public static NameEnumType from(final Class<?> fieldType) {
        if (!Enum.class.isAssignableFrom(fieldType)) {
            throw errorJavaType(NameEnumType.class, fieldType);
        }
        if (CodeEnum.class.isAssignableFrom(fieldType)) {
            String m = String.format("enum %s implements %s,please use %s."
                    , fieldType.getName(), CodeEnum.class.getName(), CodeEnumType.class.getName());
            throw new IllegalArgumentException(m);
        }
        if (TextEnum.class.isAssignableFrom(fieldType)) {
            String m = String.format("enum %s implements %s,please use %s."
                    , fieldType.getName(), TextEnum.class.getName(), TextEnumType.class.getName());
            throw new IllegalArgumentException(m);
        }
        final Class<?> actualType;
        if (fieldType.isAnonymousClass()) {
            actualType = fieldType.getSuperclass();
        } else {
            actualType = fieldType;
        }
        return INSTANCE_MAP.computeIfAbsent(actualType, NameEnumType::new);
    }

    private static final ConcurrentMap<Class<?>, NameEnumType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private final Class<?> enumClass;

    /**
     * private constructor
     */
    private NameEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
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
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
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


    public static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.ENUM;
                break;
            case PostgreSQL:
                sqlType = PostgreType.VARCHAR;
                break;
            case H2:
                sqlType = H2DataType.ENUM;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return sqlType;
    }


}
