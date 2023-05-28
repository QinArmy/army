package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.LocalDateTimeType;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

public final class PostgreTsRangeType extends PostgreSingleRangeType<LocalDateTime> {


    public static <R> PostgreTsRangeType fromFunc(final Class<? extends R> javaType,
                                                  final RangeFunction<LocalDateTime, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreTsRangeType.class, javaType);
        }
        Objects.requireNonNull(function);
        return new PostgreTsRangeType(javaType, function);
    }


    private PostgreTsRangeType(Class<?> javaType, final @Nullable RangeFunction<LocalDateTime, ?> rangeFunc) {
        super(javaType, LocalDateTime.class, rangeFunc, PostgreTsRangeType::parseDateTime);
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.TSRANGE;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        return super.arrayTypeOfThis();
    }

    @Override
    public MappingType subtype() {
        return LocalDateTimeType.INSTANCE;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        final RangeFunction<LocalDateTime, Z> rangeFunc;
        rangeFunc = PostgreRangeType.tryCreateDefaultRangeFunc(targetType, LocalDateTime.class);
        if (rangeFunc == null) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return fromFunc(targetType, rangeFunc);
    }


    @Override
    void boundToText(LocalDateTime bound, Consumer<String> consumer) {
        consumer.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
        consumer.accept(bound.format(_TimeUtils.DATETIME_FORMATTER_6));
        consumer.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
    }


    @Nullable
    static LocalDateTime parseDateTime(final String text) {
        final LocalDateTime bound;
        if (INFINITY.equalsIgnoreCase(text)) {
            bound = null;
        } else {
            bound = LocalDateTime.parse(text, _TimeUtils.DATETIME_FORMATTER_6);
        }
        return bound;
    }


}
