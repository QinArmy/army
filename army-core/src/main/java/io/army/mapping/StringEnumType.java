package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;
import io.army.struct.StringEnum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see io.army.struct.StringEnum
 * @see NameEnumType
 */
public final class StringEnumType extends AbstractMappingType {

    private static final ConcurrentMap<Class<?>, StringEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static StringEnumType create(Class<?> javaType) {
        if (!StringEnum.class.isAssignableFrom(javaType)) {
            throw errorJavaType(StringEnumType.class, javaType);
        }
        return INSTANCE_MAP.computeIfAbsent(javaType, StringEnumType::createInstance);
    }


    private final Class<?> javaType;

    private final Map<String, StringEnum> instanceMap;

    private StringEnumType(Class<?> javaType, Map<String, StringEnum> instanceMap) {
        this.javaType = javaType;
        this.instanceMap = Collections.unmodifiableMap(instanceMap);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        sqlType = NameEnumType.mapToSqlType(meta);
        if (sqlType == null) {
            throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!this.javaType.isInstance(nonNull)) {
            String m = String.format("%s isn't %s type.", nonNull.getClass().getName(), this.javaType.getName());
            throw outRangeOfSqlType(sqlType, nonNull, new CriteriaException(m));
        }
        final String name;
        name = ((StringEnum) nonNull).name();
        final StringEnum instance;
        instance = this.instanceMap.get(name);
        if (instance != nonNull) {
            String m = String.format("%s valueSet() return Set<%s> don't contain %s."
                    , this.javaType.getName(), StringEnum.class.getSimpleName(), nonNull);
            throw outRangeOfSqlType(sqlType, nonNull, new CriteriaException(m));
        }
        return name;
    }

    @Override
    public StringEnum afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final StringEnum value;
        value = this.instanceMap.get((String) nonNull);
        if (value == null) {
            throw errorValueForSqlType(sqlType, nonNull, null);
        }
        return value;
    }


    private static StringEnumType createInstance(final Class<?> javaType) {

        try {
            final Method method;
            method = javaType.getMethod("valueSet");
            final Set<?> instanceSet;
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)
                    && method.getReturnType() == Map.class) {
                instanceSet = (Set<?>) method.invoke(null);
            } else {
                String m = String.format("%s don't definite public static Set<%s> valueSet() method."
                        , javaType.getName(), StringEnum.class.getSimpleName());
                throw new IllegalArgumentException(m);
            }
            final Map<String, StringEnum> map = new HashMap<>((int) (instanceSet.size() / 0.75F));
            StringEnum stringEnum;
            for (Object instance : instanceSet) {
                if (!javaType.isInstance(instance)) {
                    String m;
                    m = String.format("%s Set<%s> valueSet() method return set element[%s] isn't %s instance."
                            , javaType.getName(), StringEnum.class.getSimpleName(), instance, javaType.getName());
                    throw new IllegalArgumentException(m);
                }
                stringEnum = (StringEnum) instance;
                if (map.putIfAbsent(stringEnum.name(), stringEnum) != null) {
                    String m = String.format("%s instance[%s] duplication.", javaType.getName(), stringEnum.name());
                    throw new IllegalArgumentException(m);
                }
            }
            return new StringEnumType(javaType, map);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String m = String.format("%s definition error.", javaType.getName());
            throw new IllegalArgumentException(m, e);
        }

    }


}
