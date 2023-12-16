package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * @see TinyBlobType
 * @see BlobType
 * @see MediumBlobType
 */
public final class LongBlobType extends _ArmyBuildInMapping implements MappingType.SqlBlobType {


    public static LongBlobType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(LongBlobType.class, fieldType);
        }
        return BYTE_ARRAY;
    }

    public static final LongBlobType BYTE_ARRAY = new LongBlobType(byte[].class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private LongBlobType(Class<?> javaType) {
        this.javaType = javaType;
    }


    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }


    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.LONGBLOB;
                break;
            case PostgreSQL:
                dataType = PostgreType.BYTEA;
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
