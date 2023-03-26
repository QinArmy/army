package io.army.mapping.optional;

import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public final class ZonedDateTimeType extends _ArmyNoInjectionMapping {

    public static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    public static ZonedDateTimeType from(Class<?> javaType) {
        if (javaType != ZonedDateTime.class) {
            throw errorJavaType(ZonedDateTimeType.class, javaType);
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
    public OffsetDateTime beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return OffsetDateTimeType.INSTANCE.beforeBind(type, env, nonNull);
    }

    @Override
    public ZonedDateTime afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof OffsetDateTime)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return ((OffsetDateTime) nonNull).toZonedDateTime();
    }


}
