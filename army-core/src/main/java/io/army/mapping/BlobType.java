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
 * @see MediumBlobType
 * @since 1.0
 */
public final class BlobType extends _ArmyBuildInMapping {

    public static final BlobType INSTANCE = new BlobType();

    public static BlobType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(BlobType.class, fieldType);
        }
        return INSTANCE;
    }


    private BlobType() {
    }


    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public SQLType map(final ServerMeta meta) {
        final SQLType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.BLOB;
                break;
            case PostgreSQL:
                type = PostgreSqlType.BYTEA;
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
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] beforeBind(SQLType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(SQLType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }


}
