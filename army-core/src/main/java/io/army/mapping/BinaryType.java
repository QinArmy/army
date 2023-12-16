package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.BinaryArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * <p>This class map {@code byte[]} to sql binary type.
 * <p>If you need to map varbinary ,you can use {@link VarBinaryType} instead of this class.
 *
 * @see VarBinaryType
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
                type = MySQLType.BINARY;
                break;
            case PostgreSQL:
                type = PostgreType.BYTEA;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BinaryArrayType.LINEAR;
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
