package io.army.mapping.mysql;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.ElementMappingType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.TextEnumType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MySQLTextEnumSetType extends MappingType implements ElementMappingType {

    private static final ConcurrentMap<Class<?>, MySQLTextEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySQLTextEnumSetType forElements(final Class<?> fieldType, final Class<?>[] elementTypes) {
        if (!Set.class.isAssignableFrom(fieldType)
                || elementTypes.length != 1
                || !elementTypes[0].isEnum()
                || !TextEnum.class.isAssignableFrom(elementTypes[0])
                || CodeEnum.class.isAssignableFrom(elementTypes[0])) {
            throw errorJavaType(MySQLTextEnumSetType.class, elementTypes[0]);
        }
        return INSTANCE_MAP.computeIfAbsent(elementTypes[0], MySQLTextEnumSetType::new);
    }

    private final List<Class<?>> elementTypes;

    private final Map<String, ? extends TextEnum> textEnumMap;

    private MySQLTextEnumSetType(Class<?> elementJavaType) {
        this.elementTypes = Collections.singletonList(elementJavaType);
        this.textEnumMap = TextEnumType.getTextMap(elementJavaType);
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
        if (meta.dialectDatabase() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySQLType.SET;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Set)) {
            throw outRangeOfSqlType(type, nonNull);
        }
        final StringBuilder builder = new StringBuilder();
        final Class<?> elementJavaType = this.elementTypes.get(0);
        int index = 0;
        for (Object e : (Set<?>) nonNull) {
            if (!elementJavaType.isInstance(e)) {
                throw valueOutRange(type, nonNull, null);
            }
            if (index > 0) {
                builder.append(_Constant.COMMA);
            }
            builder.append(((TextEnum) e).text());
            index++;
        }
        return builder.toString();
    }

    @Override
    public Set<?> afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        final String[] array = ((String) nonNull).split(",");
        final Set<TextEnum> set = new HashSet<>((int) (array.length / 0.75F));
        TextEnum textEnum;
        final Map<String, ? extends TextEnum> textEnumMap = this.textEnumMap;
        for (String text : array) {
            textEnum = textEnumMap.get(text);
            if (textEnum == null) {
                String m = String.format("%s unknown text[%s] instance.", elementTypes.get(0).getName(), text);
                throw errorValueForSqlType(type, nonNull, new IllegalArgumentException(m));
            }
            set.add(textEnum);
        }
        return set;
    }


}
