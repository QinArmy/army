package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.BlobArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

/**
 * <p>
 * This class is mapping class of {@code byte[]}.
 * </p>
 *
 * @see BinaryType
 * @see MediumBlobType
 * @since 1.0
 */
public final class BlobType extends _ArmyBuildInMapping implements MappingType.SqlBlobType {


    public static BlobType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(BlobType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BlobType INSTANCE = new BlobType();

    /**
     * private constructor
     */
    private BlobType() {
    }


    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BlobArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.BLOB;
                break;
            case PostgreSQL:
                dataType = PostgreType.BYTEA;
                break;
            case Oracle:
                dataType = OracleDataType.BLOB;
                break;
            case H2:
                dataType = H2DataType.VARBINARY;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return dataType;
    }


    @Override
    public byte[] convert(MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof byte[])) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
        }
        return (byte[]) source;
    }

    @Override
    public byte[] beforeBind(DataType dataType, MappingEnv env, final Object source) {
        if (!(source instanceof byte[])) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return (byte[]) source;
    }

    @Override
    public byte[] afterGet(DataType dataType, MappingEnv env, final Object source) {
        if (!(source instanceof byte[])) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return (byte[]) source;
    }


}
