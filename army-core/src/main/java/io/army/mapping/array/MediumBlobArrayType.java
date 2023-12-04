package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.MediumBlobType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

public final class MediumBlobArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static MediumBlobArrayType from(final Class<?> javaType) {
        final MediumBlobArrayType instance;

        if (javaType == byte[][].class) {
            instance = LINEAR;
        } else if (!javaType.isArray() || ArrayUtils.dimensionOf(javaType) < 2) {
            throw errorJavaType(MediumBlobArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == byte.class) {
            instance = new MediumBlobArrayType(javaType);
        } else {
            throw errorJavaType(MediumBlobArrayType.class, javaType);
        }
        return instance;
    }

    public static MediumBlobArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final MediumBlobArrayType UNLIMITED = new MediumBlobArrayType(Object.class);

    public static final MediumBlobArrayType LINEAR = new MediumBlobArrayType(byte[][].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private MediumBlobArrayType(Class<?> javaType) {
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
            instance = MediumBlobType.INSTANCE;
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
        return BlobArrayType.mapToSqlType(this, meta);
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


}
