package io.army.mapping;

import io.army.Database;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
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

    public static TrueFalseType create(Class<?> javaType) {
        if (javaType != Boolean.class) {
            throw errorJavaType(TrueFalseType.class, javaType);
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
                sqlType = MySqlType.CHAR;
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
    public Object beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        if (!(nonNull instanceof Boolean)) {
            String m = String.format("%s support only %s", TrueFalseType.class.getName(), Boolean.class.getName());
            throw outRangeOfSqlType(sqlType, nonNull, new CriteriaException(m));
        }
        final Object value;
        if (sqlType.database() == Database.PostgreSQL) {
            value = nonNull;
        } else {
            value = ((Boolean) nonNull) ? T : F;
        }
        return value;
    }

    @Override
    public Boolean afterGet(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        final Boolean value;
        if (sqlType.database() == Database.PostgreSQL) {
            if (!(nonNull instanceof Boolean)) {
                throw errorJavaTypeForSqlType(sqlType, nonNull);
            }
            value = (Boolean) nonNull;
        } else {
            if (!(nonNull instanceof String)) {
                throw errorJavaTypeForSqlType(sqlType, nonNull);
            }
            if (T.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.TRUE;
            } else if (F.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.FALSE;
            } else {
                throw errorJavaTypeForSqlType(sqlType, nonNull);
            }
        }
        return value;
    }


}
