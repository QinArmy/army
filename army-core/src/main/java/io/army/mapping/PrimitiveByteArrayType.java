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
public final class PrimitiveByteArrayType extends _ArmyInnerMapping implements AbstractMappingType.SqlBinaryType {

    public static final PrimitiveByteArrayType INSTANCE = new PrimitiveByteArrayType();

    public static PrimitiveByteArrayType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(PrimitiveByteArrayType.class, fieldType);
        }
        return INSTANCE;
    }

    private PrimitiveByteArrayType() {
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
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.VARBINARY;
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
            throw PARAM_ERROR_HANDLER.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw PARAM_ERROR_HANDLER.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw DATA_ACCESS_ERROR_HANDLER.apply(this, nonNull);
        }
        return (byte[]) nonNull;
    }


}
