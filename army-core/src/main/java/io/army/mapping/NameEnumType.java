package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.sqltype.H2DataType;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class NameEnumType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, NameEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static NameEnumType create(Class<?> javaType) {
        if (!javaType.isEnum()) {
            throw errorJavaType(NameEnumType.class, javaType);
        }
        return INSTANCE_MAP.computeIfAbsent(javaType, NameEnumType::new);
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
    public String beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        if (!this.enumClass.isInstance(nonNull)) {
            String m = String.format("%s isn't %s type.", nonNull.getClass().getName(), this.enumClass.getName());
            throw outRangeOfSqlType(sqlType, nonNull, new CriteriaException(m));
        }
        return ((Enum<?>) nonNull).name();
    }

    @Override
    public Enum<?> afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        try {
            return valueOf(this.enumClass, (String) nonNull);
        } catch (IllegalArgumentException e) {
            throw errorValueForSqlType(sqlType, nonNull, e);
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
                sqlType = MySqlType.ENUM;
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
