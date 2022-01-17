package io.army.mapping.mysql;

import io.army.dialect.Constant;
import io.army.dialect.Database;
import io.army.dialect.DialectEnvironment;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MySqlSetType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, MySqlSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySqlSetType create(Class<?> javaType) {
        if (!javaType.isEnum()) {
            throw createNotSupportJavaTypeException(MySqlSetType.class, javaType);
        }
        return INSTANCE_MAP.computeIfAbsent(javaType, MySqlSetType::new);
    }


    private final Class<?> javaType;

    private MySqlSetType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        if (serverMeta.database() != Database.MySQL) {
            throw noMappingError(serverMeta);
        }
        return MySqlType.SET;
    }

    @Override
    public String convertBeforeBind(SqlType sqlDataType, DialectEnvironment env, Object nonNull) {
        return literal(nonNull);
    }

    @Override
    public Set<?> convertAfterGet(SqlType sqlDataType, DialectEnvironment env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return doAfterGet(this.javaType, (String) nonNull);
    }


    public String literal(Object nonNull) {
        if (!(nonNull instanceof Set)) {
            throw notSupportConvertBeforeBind(nonNull);
        }
        final Class<?> javaType = this.javaType;
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (Object e : (Set<?>) nonNull) {
            if (!javaType.isInstance(e)) {
                throw notSupportConvertBeforeBind(nonNull);
            }
            if (index > 0) {
                builder.append(Constant.COMMA);
            }
            builder.append(((Enum<?>) e).name());
            index++;
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> Set<E> doAfterGet(Class<?> javaType, String values) {
        final String[] array = values.split(",");
        final Set<E> set = new HashSet<>((int) (array.length / 0.75F));
        for (String e : array) {
            set.add(Enum.valueOf((Class<E>) javaType, e));
        }
        return EnumSet.copyOf(set);
    }


}
