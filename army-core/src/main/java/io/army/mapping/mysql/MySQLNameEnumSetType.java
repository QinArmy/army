package io.army.mapping.mysql;

import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.ElementMappingType;
import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public final class MySQLNameEnumSetType extends _ArmyNoInjectionMapping implements ElementMappingType {

    private static final ConcurrentMap<Class<?>, MySQLNameEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySQLNameEnumSetType forElements(Class<?> fieldType, Class<?>[] elementTypes) {
        if (fieldType != Set.class) {
            throw errorJavaType(MySQLNameEnumSetType.class, fieldType);
        } else if (elementTypes.length != 1 || !elementTypes[0].isEnum()) {
            throw errorJavaType(MySQLNameEnumSetType.class, elementTypes[0]);
        }
        return INSTANCE_MAP.computeIfAbsent(elementTypes[0], MySQLNameEnumSetType::new);
    }


    private final List<Class<?>> elementTypes;

    private MySQLNameEnumSetType(Class<?> elementJavaType) {
        this.elementTypes = Collections.singletonList(elementJavaType);
    }

    @Override
    public Class<?> javaType() {
        return Set.class;
    }

    @Override
    public List<Class<?>> elementTypes() {
        return this.elementTypes;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySqlType.SET;
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Set)) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        final StringBuilder builder = new StringBuilder();
        final Class<?> elementJavaType = this.elementTypes.get(0);
        int index = 0;
        for (Object e : (Set<?>) nonNull) {
            if (!elementJavaType.isInstance(e)) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            if (index > 0) {
                builder.append(_Constant.COMMA);
            }
            builder.append(((Enum<?>) e).name());
            index++;
        }
        return builder.toString();
    }

    @Override
    public Set<?> afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        try {
            return parseToSet(this.elementTypes.get(0), (String) nonNull);
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
