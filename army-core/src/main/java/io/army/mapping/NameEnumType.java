package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.H2DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see TextEnumType
 */
public final class NameEnumType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, NameEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();

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

    private final Class<?> enumClass;

    private NameEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        return mapToSqlEnumType(this, meta);
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Enum<?> convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return this.convertToEnum(nonNull);
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return this.convertToEnum(nonNull).name();
    }

    @Override
    public Enum<?> afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        try {
            return valueOf(this.enumClass, (String) nonNull);
        } catch (IllegalArgumentException e) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
    }


    private Enum<?> convertToEnum(final Object nonNull) {
        final Enum<?> value;
        if (nonNull instanceof String) {
            value = valueOf(this.enumClass, (String) nonNull);
        } else if (this.enumClass.isInstance(nonNull)) {
            value = (Enum<?>) nonNull;
        } else {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T valueOf(Class<?> javaType, String name) {
        if (!javaType.isEnum()) {
            throw new IllegalArgumentException("not enum type.");
        }
        return Enum.valueOf((Class<T>) javaType, name);
    }


    static SqlType mapToSqlEnumType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.dialectDatabase()) {
            case MySQL:
                sqlType = MySQLType.ENUM;
                break;
            case Postgre:
                sqlType = PostgreSqlType.VARCHAR;
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
