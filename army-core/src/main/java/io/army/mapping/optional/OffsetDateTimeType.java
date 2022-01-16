package io.army.mapping.optional;

import io.army.dialect.NotSupportDialectException;
import io.army.mapping.AbstractMappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public final class OffsetDateTimeType extends _ArmyNoInjectionMapping {

    public static final OffsetDateTimeType INSTANCE = new OffsetDateTimeType();

    public static OffsetDateTimeType build(Class<?> javaType) {
        if (javaType != OffsetDateTime.class) {
            throw AbstractMappingType.createNotSupportJavaTypeException(OffsetDateTimeType.class, javaType);
        }
        return INSTANCE;
    }


    private OffsetDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP_WITH_TIMEZONE;
    }


    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case PostgreSQL:
                sqlDataType = PostgreDataType.TIMESTAMPTZ;
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
    public Object convertBeforeBind(SqlType sqlDataType, final Object nonNull) {
        final OffsetDateTime value;
        if (nonNull instanceof OffsetDateTime) {
            value = (OffsetDateTime) nonNull;
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime();
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, final Object nonNull) {
        if (!(nonNull instanceof OffsetDateTime)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
