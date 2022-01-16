package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;

public final class StringType extends AbstractMappingType {


    public static final StringType INSTANCE = new StringType();

    public static StringType build(Class<?> javaType) {
        if (javaType != String.class) {
            throw createNotSupportJavaTypeException(StringType.class, javaType);
        }
        return INSTANCE;
    }

    private StringType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.VARCHAR;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySqlType.VARCHAR;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.VARCHAR;
                break;
            case Oracle:
                sqlDataType = OracleDataType.VARCHAR2;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        final String value;
        if (nonNull instanceof String) {
            value = (String) nonNull;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
