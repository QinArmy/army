package io.army.mapping.mysql;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.MultiGenericsMappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.struct.TextEnum;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MySqlTextEnumSetType extends MappingType implements MultiGenericsMappingType {

    private static final ConcurrentMap<Class<?>, MySqlTextEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySqlTextEnumSetType fromSet(final Class<?> fieldType, final Class<?> elementTypes) {
        throw new UnsupportedOperationException();
    }

    private final List<Class<?>> elementTypes;

    private final Map<String, ? extends TextEnum> textEnumMap;

    private MySqlTextEnumSetType(Class<?> elementJavaType) {
        this.elementTypes = Collections.singletonList(elementJavaType);
        this.textEnumMap = TextEnum.getTextToEnumMap(elementJavaType);
    }


    @Override
    public Class<?> javaType() {
        return Set.class;
    }

    @Override
    public List<Class<?>> genericsTypeList() {
        return this.elementTypes;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        if (meta.serverDatabase() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySQLType.SET;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        if (!(source instanceof Set)) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        final StringBuilder builder = new StringBuilder();
        final Class<?> elementJavaType = this.elementTypes.get(0);
        int index = 0;
        for (Object e : (Set<?>) source) {
            if (!elementJavaType.isInstance(e)) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
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
    public Set<?> afterGet(DataType dataType, MappingEnv env, Object source) {
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        final String[] array = ((String) source).split(",");
        final Set<TextEnum> set = new HashSet<>((int) (array.length / 0.75F));
        TextEnum textEnum;
        final Map<String, ? extends TextEnum> textEnumMap = this.textEnumMap;
        for (String text : array) {
            textEnum = textEnumMap.get(text);
            if (textEnum == null) {
                String m = String.format("%s unknown text[%s] instance.", elementTypes.get(0).getName(), text);
                throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, new IllegalArgumentException(m));
            }
            set.add(textEnum);
        }
        return set;
    }


}
