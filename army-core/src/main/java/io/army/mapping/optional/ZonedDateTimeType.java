package io.army.mapping.optional;

import io.army.dialect.NotSupportDialectException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public final class ZonedDateTimeType extends _ArmyNoInjectionMapping {


    public static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    public static ZonedDateTimeType build(Class<?> typeClass) {
        if (typeClass != ZonedDateTime.class) {
            throw createNotSupportJavaTypeException(ZonedDateTimeType.class, typeClass);
        }
        return INSTANCE;
    }


    private ZonedDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return ZonedDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP_WITH_TIMEZONE;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        return OffsetDateTimeType.INSTANCE.sqlType(serverMeta);
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        return OffsetDateTimeType.INSTANCE.convertBeforeBind(sqlDataType, nonNull);
    }

    @Override
    public ZonedDateTime convertAfterGet(SqlType sqlDataType, final Object nonNull) {
        if (!(nonNull instanceof OffsetDateTime)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return ((OffsetDateTime) nonNull).toZonedDateTime();
    }


}
