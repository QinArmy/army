package io.army.mapping.mysql;

import io.army.Database;
import io.army.dialect.Constant;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MySQLSetType extends AbstractMappingType {

    private static final ConcurrentMap<Class<?>, MySQLSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySQLSetType create(Class<?> elementJavaType) {
        if (!elementJavaType.isEnum()) {
            throw errorJavaType(MySQLSetType.class, elementJavaType);
        }
        return INSTANCE_MAP.computeIfAbsent(elementJavaType, MySQLSetType::new);
    }


    private final Class<?> elementJavaType;

    private MySQLSetType(Class<?> elementJavaType) {
        this.elementJavaType = elementJavaType;
    }

    @Override
    public Class<?> javaType() {
        return Set.class;
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
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
        int index = 0;
        for (Object e : (Set<?>) nonNull) {
            if (!this.elementJavaType.isInstance(e)) {
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
