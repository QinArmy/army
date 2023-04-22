package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PgSqlType;
import io.army.sqltype.SqlType;

public final class SQLCharType extends MappingType {

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
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLType.CHAR;
                break;
            case PostgreSQL:
                type = PgSqlType.CHAR;
                break;
            case Oracle:
                type = OracleDataType.CHAR;
                break;

            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }


    @Override
    public String convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType._convertToString(this, this.map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return StringType._convertToString(this, type, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return StringType._convertToString(this, type, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
