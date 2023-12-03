package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.BlobType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

/**
 * @see BlobType
 */
public final class BlobArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static BlobArrayType from(final Class<?> javaType) {
        final BlobArrayType instance;

        if (javaType == byte[][].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray() || ArrayUtils.dimensionOf(javaType) < 2) {
            throw errorJavaType(BlobArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == byte.class) {
            instance = new BlobArrayType(javaType);
        } else {
            throw errorJavaType(BlobArrayType.class, javaType);
        }
        return instance;
    }


    public static final BlobArrayType UNLIMITED = new BlobArrayType(Object.class);

    public static final BlobArrayType LINEAR = new BlobArrayType(byte[][].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private BlobArrayType(Class<?> javaType) {
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
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            instance = this;
        } else if (javaType == byte[][].class) {
            instance = BlobType.INSTANCE;
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
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, PostgreArrays::parseBytea,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.byteaArrayToText(this, dataType, source, new StringBuilder(), PARAM_ERROR_HANDLER)
                .toString();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, PostgreArrays::parseBytea,
                PARAM_ERROR_HANDLER);
    }


    /*-------------------below static methods -------------------*/

    static DataType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.TEXT_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }

}
