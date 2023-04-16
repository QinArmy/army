package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;


/**
 * <p>
 * This class is mapping class of {@code byte[]}.
 * </p>
 *
 * @see PrimitiveByteArrayType
 * @see BlobType
 * @since 1.0
 */
public final class MediumBlobType extends _ArmyInnerMapping {


    public static final MediumBlobType INSTANCE = new MediumBlobType();

    public static MediumBlobType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(MediumBlobType.class, fieldType);
        }
        return INSTANCE;
    }


    private MediumBlobType() {
    }


    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.MEDIUMBLOB;
                break;
            case PostgreSQL:
                type = PostgreTypes.BYTEA;
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
    public byte[] convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof byte[])) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }


}
