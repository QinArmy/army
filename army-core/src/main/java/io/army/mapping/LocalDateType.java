package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * @see LocalDate
 */
public final class LocalDateType extends _ArmyNoInjectionMapping {


    public static final LocalDateType INSTANCE = new LocalDateType();

    public static LocalDateType from(final Class<?> fieldType) {
        if (fieldType != LocalDate.class) {
            throw errorJavaType(LocalDateType.class, fieldType);
        }
        return INSTANCE;
    }


    private LocalDateType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.DATE;
                break;
            case PostgreSQL:
                sqlType = PostgreType.DATE;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public LocalDate beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDate.parse((String) nonNull);
            } catch (DateTimeException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public LocalDate afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof LocalDate)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (LocalDate) nonNull;
    }


}
