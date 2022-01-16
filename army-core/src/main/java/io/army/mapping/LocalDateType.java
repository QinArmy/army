package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;
import java.time.LocalDate;

public final class LocalDateType extends _ArmyNoInjectionMapping {


    public static final LocalDateType INSTANCE = new LocalDateType();

    public static LocalDateType build(Class<?> javaType) {
        if (javaType != LocalDate.class) {
            throw createNotSupportJavaTypeException(LocalDateType.class, javaType);
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
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySqlType.DATE;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.DATE;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof LocalDate)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }
    
    
}
