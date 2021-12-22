package io.army.mapping.optional;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;

public final class UnsignedShortType extends _ArmyNoInjectionMapping {

    public static final UnsignedShortType INSTANCE = new UnsignedShortType();

    public static UnsignedShortType build(Class<?> javaType) {
        if (javaType != Integer.class) {
            throw createNotSupportJavaTypeException(UnsignedShortType.class, javaType);
        }
        return INSTANCE;
    }


    private UnsignedShortType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.SMALLINT;
    }

    @Override
    public SqlDataType sqlDataType(final ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        if (serverMeta.database() == Database.MySQL) {
            sqlDataType = MySQLDataType.SMALLINT_UNSIGNED;
        } else {
            throw noMappingError(serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final int value;
        if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue();
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? 1 : 0;
        } else if (nonNull instanceof String) {
            try {
                value = Integer.parseInt((String) nonNull);
            } catch (NumberFormatException e) {
                throw outRangeOfType(nonNull, e);
            }
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        if (value < 0 || value > 0xFFFF) {
            throw outRangeOfType(nonNull, null);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        final Integer value = (Integer) nonNull;
        if (value < 0 || value > 0xFFFF) {
            throw outRangeOfType(nonNull, null);
        }
        return value;
    }


}
