package io.army.mapping.optional;

import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.mapping.StringType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class SQLCharType extends AbstractMappingType {

    public static final SQLCharType INSTANCE = new SQLCharType();

    public static SQLCharType from(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(SQLCharType.class, javaType);
        }
        return INSTANCE;
    }

    private SQLCharType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.database()) {
            case MySQL:
                sqlDataType = MySqlType.CHAR;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.CHAR;
                break;
            case Oracle:
                sqlDataType = OracleDataType.CHAR;
                break;

            case H2:
            default:
                throw noMappingError(meta);

        }
        return sqlDataType;
    }


    @Override
    public String beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        return StringType.beforeBind(sqlType, nonNull);
    }

    @Override
    public String afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        return StringType.INSTANCE.afterGet(sqlType, env, nonNull);
    }


}
