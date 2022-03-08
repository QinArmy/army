package io.army.mapping.mysql;

import io.army.Database;
import io.army.dialect.Constant;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.ElementMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.mapping.TextEnumType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;
import io.army.struct.TextEnum;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MySQLTextEnumSetType extends AbstractMappingType implements ElementMappingType {

    private static final ConcurrentMap<Class<?>, MySQLTextEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySQLTextEnumSetType create(Class<?> javaType, Class<?> elementJavaType) {
        if (javaType != Set.class) {
            throw errorJavaType(MySQLTextEnumSetType.class, javaType);
        } else if (!elementJavaType.isEnum() || !TextEnum.class.isAssignableFrom(elementJavaType)) {
            throw errorJavaType(MySQLTextEnumSetType.class, elementJavaType);
        }
        return INSTANCE_MAP.computeIfAbsent(elementJavaType, MySQLTextEnumSetType::new);
    }


    private final Class<?> elementJavaType;

    private final Map<String, ? extends TextEnum> textEnumMap;

    private MySQLTextEnumSetType(Class<?> elementJavaType) {
        this.elementJavaType = elementJavaType;
        this.textEnumMap = TextEnumType.getTextMap(elementJavaType);
    }


    @Override
    public Class<?> elementType() {
        return this.elementJavaType;
    }

    @Override
    public Class<?> javaType() {
        return Set.class;
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
            builder.append(((TextEnum) e).text());
            index++;
        }
        return builder.toString();
    }

    @Override
    public Set<?> afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final String[] array = ((String) nonNull).split(",");
        final Set<TextEnum> set = new HashSet<>((int) (array.length / 0.75F));
        TextEnum textEnum;
        final Map<String, ? extends TextEnum> textEnumMap = this.textEnumMap;
        for (String text : array) {
            textEnum = textEnumMap.get(text);
            if (textEnum == null) {
                String m = String.format("%s unknown text[%s] instance.", this.elementJavaType.getName(), text);
                throw errorValueForSqlType(sqlType, nonNull, new IllegalArgumentException(m));
            }
            set.add(textEnum);
        }
        return set;
    }


}
