package io.army.mapping.optional;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;

public final class UnsignedByteType extends _ArmyNoInjectionMapping {

    public static final UnsignedByteType INSTANCE = new UnsignedByteType();

    public static UnsignedByteType build(Class<?> javaType) {
        if (javaType != Short.class) {
            throw createNotSupportJavaTypeException(UnsignedByteType.class, javaType);
        }
        return INSTANCE;
    }


    private UnsignedByteType() {
    }


    @Override
    public Class<?> javaType() {
        return Short.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TINYINT;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        if (serverMeta.database() == Database.MySQL) {
            sqlDataType = MySQLDataType.TINYINT_UNSIGNED;
        } else {
            throw noMappingError(serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, Object nonNull) {
        final short value;
        if (nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).shortValue();
        } else if (nonNull instanceof Boolean) {
            value = (short) ((Boolean) nonNull ? 1 : 0);
        } else if (nonNull instanceof String) {
            try {
                value = Short.parseShort((String) nonNull);
            } catch (NumberFormatException e) {
                throw outRangeOfType(nonNull, e);
            }
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        if (value < 0 || value > 0xFF) {
            throw outRangeOfType(nonNull, null);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Short)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        final Short value = (Short) nonNull;
        if (value < 0 || value > 0xFF) {
            throw outRangeOfType(nonNull, null);
        }
        return value;
    }


}
