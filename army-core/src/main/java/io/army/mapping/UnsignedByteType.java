package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * @see Short
 */
public final class UnsignedByteType extends _ArmyNoInjectionMapping {

    public static final UnsignedByteType INSTANCE = new UnsignedByteType();

    public static UnsignedByteType create(Class<?> javaType) {
        if (javaType != Short.class) {
            throw errorJavaType(UnsignedByteType.class, javaType);
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
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.TINYINT_UNSIGNED;
                break;
            case PostgreSQL:
                sqlType = PostgreType.SMALLINT;
                break;
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Short beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return (short) IntegerType.beforeBind(sqlType, nonNull, 0, 0xFF);
    }

    @Override
    public Short afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        switch (sqlType.database()) {
            case MySQL:
            case PostgreSQL: {
                if (!(nonNull instanceof Short)) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
            }
            break;
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final Short value = (Short) nonNull;
        if (value < 0 || value > 0xFF) {
            throw errorValueForSqlType(sqlType, nonNull, null);
        }
        return value;
    }


}
