package io.army.mapping.optional;

import io.army.dialect.NotSupportDialectException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;
import java.time.OffsetTime;

public final class OffsetTimeType extends _ArmyNoInjectionMapping {


    public static final OffsetTimeType INSTANCE = new OffsetTimeType();

    public static OffsetTimeType build(Class<?> typeClass) {
        if (typeClass != OffsetTime.class) {
            throw createNotSupportJavaTypeException(OffsetTimeType.class, typeClass);
        }
        return INSTANCE;
    }

    private OffsetTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIME_WITH_TIMEZONE;
    }


    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case PostgreSQL:
                sqlDataType = PostgreDataType.TIMETZ;
                break;
            case Oracle:
                sqlDataType = OracleDataType.TIMESTAMPTZ;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, Object nonNull) {
        final OffsetTime value;
        if (nonNull instanceof OffsetTime) {
            value = (OffsetTime) nonNull;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof OffsetTime)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
