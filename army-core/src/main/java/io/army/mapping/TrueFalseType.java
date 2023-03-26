package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * {@link Boolean}
 */
public final class TrueFalseType extends _ArmyNoInjectionMapping {

    public static final String T = "T";

    public static final String F = "F";

    public static final TrueFalseType INSTANCE = new TrueFalseType();

    public static TrueFalseType from(final Class<?> fieldType) {
        if (fieldType != Boolean.class) {
            throw errorJavaType(TrueFalseType.class, fieldType);
        }
        return INSTANCE;
    }


    private TrueFalseType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.CHAR;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BOOLEAN;
                break;
            case Oracle:
                sqlType = OracleDataType.CHAR;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof Boolean)) {
            String m = String.format("%s support only %s", TrueFalseType.class.getName(), Boolean.class.getName());
            throw outRangeOfSqlType(type, nonNull, new CriteriaException(m));
        }
        final Object value;
        if (type.database() == Database.PostgreSQL) {
            value = nonNull;
        } else {
            value = ((Boolean) nonNull) ? T : F;
        }
        return value;
    }

    @Override
    public Boolean afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        final Boolean value;
        if (type.database() == Database.PostgreSQL) {
            if (!(nonNull instanceof Boolean)) {
                throw errorJavaTypeForSqlType(type, nonNull);
            }
            value = (Boolean) nonNull;
        } else {
            if (!(nonNull instanceof String)) {
                throw errorJavaTypeForSqlType(type, nonNull);
            }
            if (T.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.TRUE;
            } else if (F.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.FALSE;
            } else {
                throw errorJavaTypeForSqlType(type, nonNull);
            }
        }
        return value;
    }


}
