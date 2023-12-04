package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.LongType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;

public final class LongArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static LongArrayType from(final Class<?> arrayClass) {
        final LongArrayType instance;
        final Class<?> componentType;
        if (arrayClass == Long[].class) {
            instance = LINEAR;
        } else if (arrayClass == long[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(LongArrayType.class, arrayClass);
        } else if ((componentType = ArrayUtils.underlyingComponent(arrayClass)) == long.class
                || componentType == Long.class) {
            instance = new LongArrayType(arrayClass, componentType);
        } else {
            throw errorJavaType(LongArrayType.class, arrayClass);
        }
        return instance;
    }

    public static LongArrayType fromUnlimited(final Class<?> longClass) {
        final LongArrayType instance;
        if (longClass == Long.class) {
            instance = UNLIMITED;
        } else if (longClass == long.class) {
            instance = PRIMITIVE_UNLIMITED;
        } else {
            throw errorJavaType(LongArrayType.class, longClass);
        }
        return instance;
    }

    public static final LongArrayType UNLIMITED = new LongArrayType(Object.class, Long.class);

    public static final LongArrayType LINEAR = new LongArrayType(Long[].class, Long.class);

    public static final LongArrayType PRIMITIVE_UNLIMITED = new LongArrayType(Object.class, long.class);

    public static final LongArrayType PRIMITIVE_LINEAR = new LongArrayType(long[].class, long.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;


    /**
     * private constructor
     */
    private LongArrayType(final Class<?> javaType, Class<?> underlyingJavaType) {
        this.javaType = javaType;
        this.underlyingJavaType = underlyingJavaType;
    }


    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return this.underlyingJavaType;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == Long[].class || javaType == long[].class) {
            instance = LongType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
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
        return mapToSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        final boolean nonNull = this.underlyingJavaType == long.class;
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, LongArrayType::parseText,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, LongArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == long.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, LongArrayType::parseText, ACCESS_ERROR_HANDLER);
    }


    /*-------------------below static methods -------------------*/

    static DataType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.BIGINT_ARRAY;
                break;
            case Oracle:
            case H2:
            case MySQL:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }


    private static long parseText(final String text, final int offset, final int end) {
        return Long.parseLong(text.substring(offset, end));
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof Long)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(element.toString());
    }


}
