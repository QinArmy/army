package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;

public final class SQLCharType extends _ArmyBuildInMapping implements MappingType.SqlStringType {

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
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public SQLType map(final ServerMeta meta) {
        final SQLType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.CHAR;
                break;
            case PostgreSQL:
                type = PostgreSqlType.CHAR;
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
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public String convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType._convertToString(this, this.map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String beforeBind(SQLType type, MappingEnv env, Object nonNull) {
        return StringType._convertToString(this, type, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String afterGet(SQLType type, MappingEnv env, Object nonNull) {
        return StringType._convertToString(this, type, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
