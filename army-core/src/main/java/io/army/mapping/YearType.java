package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;
import java.time.Year;

public final class YearType extends _ArmyNoInjectionMapping {

    public static final YearType INSTANCE = new YearType();

    public static YearType build(Class<?> javaType) {
        if (javaType != Year.class) {
            throw createNotSupportJavaTypeException(YearType.class, javaType);
        }
        return INSTANCE;
    }

    private YearType() {
    }

    @Override
    public Class<?> javaType() {
        return Year.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.YEAR;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.INTEGER;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final int value;
        if (nonNull instanceof Year) {
            value = ((Year) nonNull).getValue();
        } else if (nonNull instanceof Integer || nonNull instanceof Short) {
            value = ((Number) nonNull).intValue();
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        final Year value;
        switch (sqlDataType.database()) {
            case MySQL: {
                if (!(nonNull instanceof Year)) {
                    throw notSupportConvertAfterGet(nonNull);
                }
                value = (Year) nonNull;
            }
            break;
            case PostgreSQL:
            case H2:
            case Oracle: {
                if (!(nonNull instanceof Integer)) {
                    throw notSupportConvertAfterGet(nonNull);
                }
                value = Year.of((Integer) nonNull);
            }
            break;
            default:
                throw notSupportConvertAfterGet(nonNull);
        }
        return value;
    }


}
