package io.army.mapping;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;
import io.army.util._Exceptions;

import java.sql.JDBCType;

public final class TrueFalseType extends _ArmyNoInjectionMapping {

    public static final String T = "T";

    public static final String F = "F";

    public static final TrueFalseType INSTANCE = new TrueFalseType();

    public static TrueFalseType build(Class<?> typeClass) {
        if (typeClass != Boolean.class) {
            throw AbstractMappingType.createNotSupportJavaTypeException(TrueFalseType.class, typeClass);
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
    public JDBCType jdbcType() {
        return JDBCType.BOOLEAN;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.CHAR;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.BOOLEAN;
                break;
            case Oracle:
                sqlDataType = OracleDataType.CHAR;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        if (!(nonNull instanceof Boolean)) {
            throw _Exceptions.javaTypeUnsupportedByMapping(this, nonNull);
        }
        final Object value;
        if (sqlDataType.database() == Database.PostgreSQL) {
            value = nonNull;
        } else {
            value = ((Boolean) nonNull) ? T : F;
        }
        return value;
    }

    @Override
    public Boolean convertAfterGet(SqlDataType sqlDataType, final Object nonNull) {
        final Boolean value;
        if (sqlDataType.database() == Database.PostgreSQL) {
            if (!(nonNull instanceof Boolean)) {
                throw notSupportConvertAfterGet(nonNull);
            }
            value = (Boolean) nonNull;
        } else {
            if (!(nonNull instanceof String)) {
                throw notSupportConvertAfterGet(nonNull);
            }
            if (T.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.TRUE;
            } else if (F.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.FALSE;
            } else {
                throw outRangeOfType(nonNull, null);
            }
        }
        return value;
    }


}
