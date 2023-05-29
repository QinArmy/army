package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.LocalDateTimeType;
import io.army.mapping.MappingType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 * This class representing Postgre tsrange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">tsrange</a>
 */
public final class PostgreTsRangeType extends PostgreSingleRangeType<LocalDateTime> {

    /**
     * @throws MetaException when javaType isn't {@link String#getClass()} and no 'create' static factory method.
     * @see #fromMethod(Class, String)
     */
    public static PostgreTsRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreTsRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }


    public static <R> PostgreTsRangeType fromFunc(final Class<? extends R> javaType,
                                                  final RangeFunction<LocalDateTime, R> function)
            throws IllegalArgumentException {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreTsRangeType.class, javaType);
        }
        Objects.requireNonNull(function);
        return new PostgreTsRangeType(javaType, function);
    }

    /**
     * @throws MetaException when javaType or methodName error
     */
    public static PostgreTsRangeType fromMethod(final Class<?> javaType, final String methodName) throws MetaException {
        return new PostgreTsRangeType(javaType, createRangeFunction(javaType, LocalDateTime.class, methodName));
    }

    static PostgreTsRangeType fromArrayType(final PostgreTsRangeArrayType type) {
        final RangeFunction<LocalDateTime, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType;
        if (type instanceof PostgreTsRangeArrayType.ListType) {
            javaType = ((PostgreTsRangeArrayType.ListType<?>) type).elementType;
        } else {
            javaType = type.javaType.getComponentType();
        }
        assert !javaType.isArray();
        return new PostgreTsRangeType(javaType, rangeFunc);
    }


    public static final PostgreTsRangeType TEXT = new PostgreTsRangeType(String.class, null);

    /**
     * private constructor
     */
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
        final PostgreTsRangeArrayType arrayType;
        final RangeFunction<LocalDateTime, ?> rangeFunc = this.rangeFunc;
        if (rangeFunc == null) {
            assert this.javaType == String.class;
            arrayType = PostgreTsRangeArrayType.LINEAR;
        } else {
            arrayType = PostgreTsRangeArrayType.fromFunc(this.javaType, rangeFunc);
        }
        return arrayType;
    }

    @Override
    public MappingType subtype() {
        return LocalDateTimeType.INSTANCE;
    }

    @Override
    void boundToText(LocalDateTime bound, Consumer<String> appender) {
        appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
        appender.accept(bound.format(_TimeUtils.DATETIME_FORMATTER_6));
        appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
    }


    @Override
    Class<LocalDateTime> boundJavaType() {
        return LocalDateTime.class;
    }

    @Override
    <Z> PostgreSingleRangeType<LocalDateTime> getInstanceFrom(Class<Z> javaType,
                                                              RangeFunction<LocalDateTime, Z> rangeFunc) {
        return fromFunc(javaType, rangeFunc);
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
