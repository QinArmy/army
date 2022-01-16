package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;
import java.time.LocalTime;

public final class LocalTimeType extends _ArmyNoInjectionMapping {


    public static final LocalTimeType INSTANCE = new LocalTimeType();

    public static LocalTimeType build(Class<?> javaType) {
        if (javaType != LocalTime.class) {
            throw createNotSupportJavaTypeException(LocalTimeType.class, javaType);
        }
        return INSTANCE;
    }


    private LocalTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIME;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySqlType.TIME;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.TIME;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, final Object nonNull) {
        final LocalTime value;
        if (nonNull instanceof LocalTime) {
            value = (LocalTime) nonNull;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, final Object nonNull) {
        if (!(nonNull instanceof LocalTime)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }
    
    
    
}
