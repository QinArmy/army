package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.VarBinaryArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

/**
 * <p>This class map {@code byte[]} to sql varbinary type.
 * <p>If you need to map binary ,you can use {@link BinaryType} instead of this class.
 *
 * @see BinaryType
 */
public final class VarBinaryType extends _ArmyBuildInMapping implements MappingType.SqlBinaryType {

    public static VarBinaryType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(VarBinaryType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final VarBinaryType INSTANCE = new VarBinaryType();


    /**
     * private constructor
     */
    private VarBinaryType() {
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
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return VarBinaryArrayType.LINEAR;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        if (targetType != String.class) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return StringType.INSTANCE;
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
