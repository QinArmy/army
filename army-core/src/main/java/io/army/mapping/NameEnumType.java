package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.sqltype.H2DataType;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
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
        if (!fieldType.isEnum()) {
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
        return INSTANCE_MAP.computeIfAbsent(fieldType, NameEnumType::new);
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
        final SqlType sqlType;
        sqlType = mapToSqlType(meta);
        if (sqlType == null) {
            throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        if (!this.enumClass.isInstance(nonNull)) {
            String m = String.format("%s isn't %s type.", nonNull.getClass().getName(), this.enumClass.getName());
            throw outRangeOfSqlType(type, nonNull, new CriteriaException(m));
        }
        return ((Enum<?>) nonNull).name();
    }

    @Override
    public Enum<?> afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        try {
            return valueOf(this.enumClass, (String) nonNull);
        } catch (IllegalArgumentException e) {
            throw errorValueForSqlType(type, nonNull, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T valueOf(Class<?> javaType, String name) {
        if (!javaType.isEnum()) {
            throw new IllegalArgumentException("not enum type.");
        }
        return Enum.valueOf((Class<T>) javaType, name);
    }


    @Nullable
    public static SqlType mapToSqlType(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.ENUM;
                break;
            case PostgreSQL:
                sqlType = PostgreType.VARCHAR;
                break;
            case H2:
                sqlType = H2DataType.ENUM;
                break;
            default:
                sqlType = null;

        }
        return sqlType;
    }


}
