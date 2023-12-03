package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.BooleanType;
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

public class BooleanArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static BooleanArrayType from(final Class<?> javaType) {
        final BooleanArrayType instance;

        final Class<?> underlyingJavaType;
        if (javaType == Boolean[].class) {
            instance = LINEAR;
        } else if (javaType == boolean[].class) {
            instance = PRIMITIVE_UNLIMITED;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(BooleanArrayType.class, javaType);
        } else if ((underlyingJavaType = ArrayUtils.underlyingComponent(javaType)) == Boolean.class) {
            instance = new BooleanArrayType(javaType, Boolean.class);
        } else if (underlyingJavaType == boolean.class) {
            instance = new BooleanArrayType(javaType, boolean.class);
        } else {
            throw errorJavaType(BooleanArrayType.class, javaType);
        }
        return instance;
    }

    /**
     * unlimited dimension array of {@code boolean}
     */
    public static final BooleanArrayType PRIMITIVE_UNLIMITED = new BooleanArrayType(Object.class, boolean.class);

    /**
     * one dimension array of {@code  boolean}
     */
    public static final BooleanArrayType PRIMITIVE_LINEAR = new BooleanArrayType(boolean[].class, boolean.class);

    /**
     * one dimension array of {@link Boolean}
     */
    public static final BooleanArrayType LINEAR = new BooleanArrayType(Boolean[].class, Boolean.class);

    /**
     * unlimited dimension array of {@link Boolean}
     */
    public static final BooleanArrayType UNLIMITED = new BooleanArrayType(Object.class, Boolean.class);


    private final Class<?> javaType;

    private final Class<?> underlyingJavaType;

    /**
     * private constructor
     */
    private BooleanArrayType(Class<?> javaType, Class<?> underlyingJavaType) {
        this.javaType = javaType;
        this.underlyingJavaType = underlyingJavaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final Class<?> underlyingJavaType() {
        return this.underlyingJavaType;
    }

    @Override
    public final DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.BOOLEAN_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public final MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == Boolean[].class || javaType == boolean[].class) {
            instance = BooleanType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }

    @Override
    public final Object convert(MappingEnv env, Object source) throws CriteriaException {
        final boolean nonNull = this.underlyingJavaType == boolean.class;
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, nonNull, BooleanArrayType::parseBoolean, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, BooleanArrayType::appendToText, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        final boolean nonNull = this.underlyingJavaType == boolean.class;
        return PostgreArrays.arrayAfterGet(this, dataType, source, nonNull, BooleanArrayType::parseBoolean, ACCESS_ERROR_HANDLER);
    }


    /*-------------------below static methods -------------------*/

    private static Boolean parseBoolean(final String text, final int offset, final int end) {

        final Boolean value;
        if (text.regionMatches(true, offset, "true", 0, 4)) {
            if (offset + 4 != end) {
                throw new IllegalArgumentException("not boolean");
            }
            value = Boolean.TRUE;
        } else if (text.regionMatches(true, offset, "false", 0, 5)) {
            if (offset + 5 != end) {
                throw new IllegalArgumentException("not boolean");
            }
            value = Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("not boolean");
        }
        return value;
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof Boolean)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(element.toString());
    }


}
