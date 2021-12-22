package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;
import java.time.LocalDateTime;

public final class LocalDateTimeType extends _ArmyNoInjectionMapping {


    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType build(Class<?> typeClass) {
        if (typeClass != LocalDateTime.class) {
            throw AbstractMappingType.createNotSupportJavaTypeException(LocalDateTimeType.class, typeClass);
        }
        return INSTANCE;
    }


    private LocalDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP;
    }


    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.DATETIME;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.TIMESTAMP;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final LocalDateTime value;
        if (nonNull instanceof LocalDateTime) {
            value = (LocalDateTime) nonNull;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof LocalDateTime)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }

}
