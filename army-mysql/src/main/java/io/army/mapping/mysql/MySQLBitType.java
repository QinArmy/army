package io.army.mapping.mysql;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;

import java.util.BitSet;

/**
 * @see Long
 */
public final class MySQLBitType extends _ArmyNoInjectionMapping {

    public static final MySQLBitType INSTANCE = new MySQLBitType();

    public static MySQLBitType from(Class<?> javaType) {
        if (javaType != Long.class) {
            throw errorJavaType(MySQLBitType.class, javaType);
        }
        return INSTANCE;
    }

    private MySQLBitType() {
    }


    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySQLTypes.BIT;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Long beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return beforeBind(type, nonNull);
    }

    @Override
    public Long afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Long)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (Long) nonNull;
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
