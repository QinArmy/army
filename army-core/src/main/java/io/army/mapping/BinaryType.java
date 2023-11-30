package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

/**
 * <p>
 * This class is mapping class of {@code byte[]}.
 * </p>
 *
 * @since 1.0
 */
public final class BinaryType extends _ArmyBuildInMapping implements MappingType.SqlBinaryType {

    public static BinaryType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(BinaryType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BinaryType INSTANCE = new BinaryType();


    /**
     * private constructor
     */
    private BinaryType() {
    }

    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.VARBINARY;
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
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] beforeBind(DataType dataType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(DataType dataType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }


}
