package io.army.mapping.optional;

import io.army.dialect.NotSupportDialectException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public final class ZonedDateTimeType extends _ArmyNoInjectionMapping {


    public static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    public static ZonedDateTimeType build(Class<?> typeClass) {
        if (typeClass != ZonedDateTime.class) {
            throw errorJavaType(ZonedDateTimeType.class, typeClass);
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
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return OffsetDateTimeType.INSTANCE.map(meta);
    }

    @Override
    public Object beforeBind_(SqlType sqlType, Object nonNull) {
        return OffsetDateTimeType.INSTANCE.beforeBind_(sqlType, nonNull);
    }

    @Override
    public ZonedDateTime afterGet_(SqlType sqlType, final Object nonNull) {
        if (!(nonNull instanceof OffsetDateTime)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return ((OffsetDateTime) nonNull).toZonedDateTime();
    }


}
