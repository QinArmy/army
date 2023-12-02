package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.BinaryType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;


/**
 * @see BinaryType
 * @since 1.0
 */
public final class BinaryArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static BinaryArrayType from(final Class<?> javaType) {
        final BinaryArrayType instance;

        if (javaType == byte[][].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray() || ArrayUtils.dimensionOf(javaType) < 2) {
            throw errorJavaType(BinaryArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == byte.class) {
            instance = new BinaryArrayType(javaType);
        } else {
            throw errorJavaType(BinaryArrayType.class, javaType);
        }
        return instance;
    }


    public static final BinaryArrayType UNLIMITED = new BinaryArrayType(Object.class);

    public static final BinaryArrayType LINEAR = new BinaryArrayType(byte[][].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private BinaryArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return byte[].class;
    }


    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.BYTEA_ARRAY;
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
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            instance = this;
        } else if (javaType == byte[][].class) {
            instance = BinaryType.INSTANCE;
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
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {

        return null;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return null;
    }


    private Object toBinaryArray(DataType dataType, Object source, ErrorHandler errorHandler) {
        return null;
    }

    /*-------------------below static methods -------------------*/

    public static byte[] parseBinary(String text, int offset, int end) {
        return null;
    }

    public static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof byte[])) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(new String((byte[]) element));
    }


}
