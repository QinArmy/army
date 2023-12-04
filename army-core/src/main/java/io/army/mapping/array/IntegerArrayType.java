package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.IntegerType;
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

public class IntegerArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static IntegerArrayType from(final Class<?> arrayClass) {
        final IntegerArrayType instance;
        final Class<?> componentType;
        if (arrayClass == Integer[].class) {
            instance = LINEAR;
        } else if (arrayClass == int[].class) {
            instance = PRIMITIVE_LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(IntegerArrayType.class, arrayClass);
        } else if ((componentType = ArrayUtils.underlyingComponent(arrayClass)) == int.class
                || componentType == Integer.class) {
            instance = new IntegerArrayType(arrayClass, componentType);
        } else {
            throw errorJavaType(IntegerArrayType.class, arrayClass);
        }
        return instance;
    }

    public static IntegerArrayType fromUnlimited(final Class<?> intClass) {
        final IntegerArrayType instance;
        if (intClass == Integer.class) {
            instance = UNLIMITED;
        } else if (intClass == int.class) {
            instance = PRIMITIVE_UNLIMITED;
        } else {
            throw errorJavaType(IntegerArrayType.class, intClass);
        }
        return instance;
    }

    public static final IntegerArrayType UNLIMITED = new IntegerArrayType(Object.class, Integer.class);

    public static final IntegerArrayType LINEAR = new IntegerArrayType(Integer[].class, Integer.class);

    public static final IntegerArrayType PRIMITIVE_UNLIMITED = new IntegerArrayType(Object.class, int.class);

    public static final IntegerArrayType PRIMITIVE_LINEAR = new IntegerArrayType(int[].class, int.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;


    /**
     * private constructor
     */
    private IntegerArrayType(final Class<?> javaType, Class<?> underlyingJavaType) {
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
        } else if (javaType == Integer[].class || javaType == int[].class) {
            instance = IntegerType.INSTANCE;
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
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        return mapToDataType(this, meta);
    }


    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        final boolean nonNull = this.underlyingJavaType == int.class;
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, IntegerArrayType::parseText,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, IntegerArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == int.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, IntegerArrayType::parseText, ACCESS_ERROR_HANDLER);
    }

    /*-------------------below static methods -------------------*/

    static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.INTEGER_ARRAY;
                break;
            case Oracle:
            case H2:
            case MySQL:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }


    private static int parseText(final String text, final int offset, final int end) {
        return Integer.parseInt(text.substring(offset, end));
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof Integer)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(element.toString());
    }


}
