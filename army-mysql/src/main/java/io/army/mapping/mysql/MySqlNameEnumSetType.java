package io.army.mapping.mysql;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public final class MySqlNameEnumSetType extends _ArmyNoInjectionMapping implements MultiGenericsMappingType {

    private static final ConcurrentMap<Class<?>, MySqlNameEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySqlNameEnumSetType forElements(Class<?> fieldType, Class<?>[] elementTypes) {
        if (fieldType != Set.class) {
            throw errorJavaType(MySqlNameEnumSetType.class, fieldType);
        } else if (elementTypes.length != 1 || !elementTypes[0].isEnum()) {
            throw errorJavaType(MySqlNameEnumSetType.class, elementTypes[0]);
        }
        return INSTANCE_MAP.computeIfAbsent(elementTypes[0], MySqlNameEnumSetType::new);
    }


    private final List<Class<?>> elementTypes;

    private MySqlNameEnumSetType(Class<?> elementJavaType) {
        this.elementTypes = Collections.singletonList(elementJavaType);
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
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Set)) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        final StringBuilder builder = new StringBuilder();
        final Class<?> elementJavaType = this.elementTypes.get(0);
        int index = 0;
        for (Object e : (Set<?>) nonNull) {
            if (!elementJavaType.isInstance(e)) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
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
    public Set<?> afterGet(DataType dataType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        try {
            return parseToSet(this.elementTypes.get(0), (String) nonNull);
        } catch (IllegalArgumentException e) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, nonNull, e);
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