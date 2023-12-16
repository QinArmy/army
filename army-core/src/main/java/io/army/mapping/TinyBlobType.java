package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.MediumBlobArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

public final class TinyBlobType extends _ArmyBuildInMapping implements MappingType.SqlBlobType {


    public static TinyBlobType from(final Class<?> javaType) {
        if (javaType != byte[].class) {
            throw errorJavaType(TinyBlobType.class, javaType);
        }
        return INSTANCE;
    }

    public static final TinyBlobType INSTANCE = new TinyBlobType();

    /**
     * private constructor
     */
    private TinyBlobType() {
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
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return MediumBlobArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.TINYBLOB;
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
