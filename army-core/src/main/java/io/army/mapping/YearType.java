package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.time.Year;

/**
 * @see Year
 */
public final class YearType extends _ArmyNoInjectionMapping {

    public static final YearType INSTANCE = new YearType();

    public static YearType from(final Class<?> fieldType) {
        if (fieldType != Year.class) {
            throw errorJavaType(YearType.class, fieldType);
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
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.YEAR;
                break;
            case PostgreSQL:
                sqlType = PostgreType.INTEGER;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        final Object value;
        switch (sqlType.database()) {
            case MySQL: {
                if (nonNull instanceof Year) {
                    value = nonNull;
                } else if (nonNull instanceof Integer || nonNull instanceof Short) {
                    value = Year.of(((Number) nonNull).intValue());
                } else {
                    throw outRangeOfSqlType(sqlType, nonNull);
                }
            }
            break;
            case PostgreSQL: {
                if (!(nonNull instanceof Integer)) {
                    throw outRangeOfSqlType(sqlType, nonNull);
                }
                value = nonNull;
            }
            break;
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw outRangeOfSqlType(sqlType, nonNull);

        }
        return value;
    }

    @Override
    public Year afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        final Year value;
        switch (sqlType.database()) {
            case MySQL: {
                if (!(nonNull instanceof Year)) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
                value = (Year) nonNull;
            }
            break;
            case PostgreSQL: {
                if (!(nonNull instanceof Integer)) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
                value = Year.of((Integer) nonNull);
            }
            break;
            case H2:
            case Oracle:
            default:
                throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }


}
