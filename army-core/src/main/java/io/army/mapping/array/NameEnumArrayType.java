package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NameEnumType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util.ArrayUtils;
import io.army.util._ClassUtils;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;


/**
 * @see Enum
 * @see NameEnumType
 */
public final class NameEnumArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static NameEnumArrayType from(final Class<?> arrayClass) {
        if (!arrayClass.isArray()) {
            throw errorJavaType(NameEnumArrayType.class, arrayClass);
        }
        final Class<?> enumClass;
        enumClass = ArrayUtils.underlyingComponent(arrayClass);

        if (!Enum.class.isAssignableFrom(enumClass)
                || CodeEnum.class.isAssignableFrom(enumClass)
                || TextEnum.class.isAssignableFrom(enumClass)) {
            throw errorJavaType(NameEnumArrayType.class, arrayClass);
        }
        return INSTANCE_MAP.computeIfAbsent(arrayClass, key -> new NameEnumArrayType(arrayClass, enumClass));
    }

    public static NameEnumArrayType fromUnlimited(final Class<?> enumClass) {
        if (!Enum.class.isAssignableFrom(enumClass)
                || CodeEnum.class.isAssignableFrom(enumClass)
                || TextEnum.class.isAssignableFrom(enumClass)) {
            throw errorJavaType(NameEnumArrayType.class, enumClass);
        }
        final Class<?> actualClass;
        actualClass = _ClassUtils.getEnumClass(enumClass);
        return INSTANCE_MAP.computeIfAbsent(actualClass, key -> new NameEnumArrayType(Object.class, actualClass));
    }


    private static final ConcurrentMap<Class<?>, NameEnumArrayType> INSTANCE_MAP = _Collections.concurrentHashMap();


    private final Class<?> javaType;

    private final Class<?> enumClass;

    /**
     * private constructor
     */
    private NameEnumArrayType(Class<?> javaType, Class<?> enumClass) {
        this.javaType = javaType;
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return this.enumClass;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType, componentType;
        final MappingType instance;

        if (javaType == Object.class) {
            instance = this;
        } else if ((componentType = javaType.getComponentType()).isArray()) {
            instance = from(componentType);
        } else {
            instance = NameEnumType.from(componentType);
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return StringArrayType.mapToSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, this::parseText, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, this::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, this::parseText, ACCESS_ERROR_HANDLER);
    }

    private Enum<?> parseText(final String text, final int offset, final int end) {
        return NameEnumType.valueOf(this.enumClass, text.substring(offset, end));
    }

    private void appendToText(final Object element, final Consumer<String> appender) {
        if (!this.enumClass.isInstance(element)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(((Enum<?>) element).name());
    }


}
