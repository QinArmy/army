package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;


/**
 * <p>
 * This class is mapping class of {@code byte[]}.
 * </p>
 *
 * @see BinaryType
 * @see BlobType
 * @since 1.0
 */
public final class MediumBlobType extends _ArmyBuildInMapping {

    public static MediumBlobType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(MediumBlobType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final MediumBlobType INSTANCE = new MediumBlobType();

    /**
     * private constructor
     */
    private MediumBlobType() {
    }


    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.MEDIUMBLOB;
                break;
            case PostgreSQL:
                type = PostgreType.BYTEA;
                break;
            case Oracle:
                type = OracleDataType.BLOB;
                break;
            case H2:
                type = H2DataType.VARBINARY;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public byte[] convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof byte[])) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull, null);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] beforeBind(DataType dataType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(DataType dataType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        return (byte[]) nonNull;
    }


}
