package io.army.mapping.mysql;

import io.army.Database;
import io.army.dialect.Constant;
import io.army.mapping.ElementMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public final class MySQLNameEnumSetType extends _ArmyNoInjectionMapping implements ElementMappingType {

    private static final ConcurrentMap<Class<?>, MySQLNameEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySQLNameEnumSetType create(Class<?> javaType, Class<?> elementJavaType) {
        if (javaType != Set.class) {
            throw errorJavaType(MySQLNameEnumSetType.class, javaType);
        } else if (!elementJavaType.isEnum()) {
            throw errorJavaType(MySQLNameEnumSetType.class, elementJavaType);
        }
        return INSTANCE_MAP.computeIfAbsent(elementJavaType, MySQLNameEnumSetType::new);
    }


    private final Class<?> elementJavaType;

    private MySQLNameEnumSetType(Class<?> elementJavaType) {
        this.elementJavaType = elementJavaType;
    }

    @Override
    public Class<?> javaType() {
        return Set.class;
    }

    @Override
    public Class<?> elementType() {
        return this.elementJavaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySqlType.SET;
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof Set)) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        final StringBuilder builder = new StringBuilder();
        final Class<?> elementJavaType = this.elementJavaType;
        int index = 0;
        for (Object e : (Set<?>) nonNull) {
            if (!elementJavaType.isInstance(e)) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            if (index > 0) {
                builder.append(Constant.COMMA);
            }
            builder.append(((Enum<?>) e).name());
            index++;
        }
        return builder.toString();
    }

    @Override
    public Set<?> afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        try {
            return parseToSet(this.elementJavaType, (String) nonNull);
        } catch (IllegalArgumentException e) {
            throw errorValueForSqlType(sqlType, nonNull, e);
        }
    }


    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> Set<E> parseToSet(Class<?> javaType, String values) {
        final String[] array = values.split(",");
        final Set<E> set = new HashSet<>((int) (array.length / 0.75F));
        for (String e : array) {
            set.add(Enum.valueOf((Class<E>) javaType, e));
        }
        return set;
    }


}
