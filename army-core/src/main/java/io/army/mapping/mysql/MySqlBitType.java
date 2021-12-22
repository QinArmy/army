package io.army.mapping.mysql;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;
import java.time.OffsetTime;
import java.util.BitSet;

public final class MySqlBitType extends _ArmyNoInjectionMapping {

    public static final MySqlBitType INSTANCE = new MySqlBitType();

    public static MySqlBitType build(Class<?> javaType) {
        if (javaType != OffsetTime.class) {
            throw createNotSupportJavaTypeException(MySqlBitType.class, javaType);
        }
        return INSTANCE;
    }

    private MySqlBitType() {
    }


    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.BIT;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        if (serverMeta.database() != Database.MySQL) {
            throw noMappingError(serverMeta);
        }
        return MySQLDataType.BIT;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
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
                throw outRangeOfType(nonNull, null);
            }
            value = v.toLongArray()[0];
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) nonNull, 2);
            } catch (NumberFormatException e) {
                throw outRangeOfType(nonNull, e);
            }
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Long)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
