package io.army.mapping.mysql;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;

import java.util.BitSet;

/**
 * @see Long
 */
public final class MySqlBitType extends _ArmyNoInjectionMapping {

    public static MySqlBitType from(Class<?> javaType) {
        if (javaType != Long.class) {
            throw errorJavaType(MySqlBitType.class, javaType);
        }
        return INSTANCE;
    }

    public static final MySqlBitType INSTANCE = new MySqlBitType();

    /**
     * private constructor
     */
    private MySqlBitType() {
    }


    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        if (meta.serverDatabase() != Database.MySQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return MySQLType.BIT;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        final MappingType instance;
        if (targetType == String.class) {
            instance = StringType.INSTANCE;
        } else if (targetType == BitSet.class) {
            instance = BitSetType.INSTANCE;
        } else if (targetType == byte[].class) {
            instance = BinaryType.INSTANCE;
        } else {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return instance;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, final Object source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long afterGet(DataType dataType, MappingEnv env, Object source) {
        throw new UnsupportedOperationException();
    }

    public static long beforeBind(SqlType sqlType, final Object nonNull) {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer) {
            value = (Integer) nonNull & 0xFFFF_FFFFL;
        } else if (nonNull instanceof Short) {
            value = (Short) nonNull & 0xFFFFL;
        } else if (nonNull instanceof Byte) {
            value = (Byte) nonNull & 0xFFL;
        } else if (nonNull instanceof BitSet) {
            final BitSet v = (BitSet) nonNull;
            if (v.length() > 64) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = v.toLongArray()[0];
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) nonNull, 2);
            } catch (NumberFormatException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }


}
