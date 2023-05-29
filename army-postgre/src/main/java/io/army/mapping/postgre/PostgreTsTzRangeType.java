package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.OffsetDateTimeType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 * This class representing Postgre tstzrange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">tstzrange</a>
 */
public final class PostgreTsTzRangeType extends PostgreSingleRangeType<OffsetDateTime> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String} and no 'create' static factory method.
     */
    public static PostgreTsTzRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreTsTzRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreTsTzRangeType fromFunc(final Class<? extends R> javaType,
                                                    final RangeFunction<OffsetDateTime, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreTsTzRangeType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreTsTzRangeType(javaType, function);
    }

    /**
     * <p>
     * factory method example:
     * <pre><bre/>
     *    public static MyInt4Range create(int lowerBound,boolean includeLowerBound,int upperBound,boolean includeUpperBound){
     *        // do something
     *    }
     *     </pre>
     * </p>
     *
     * @param methodName public static factory method name,for example : com.my.Factory::create
     * @throws io.army.meta.MetaException throw when factory method name error.
     */
    public static PostgreTsTzRangeType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreTsTzRangeType.class, javaType);
        }
        return new PostgreTsTzRangeType(javaType, createRangeFunction(javaType, OffsetDateTime.class, methodName));
    }


    /**
     * package method
     */
    static PostgreTsTzRangeType fromArrayType(final PostgreTsTzRangeArrayType type) {
        final Class<?> javaType;
        final RangeFunction<OffsetDateTime, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        if (type instanceof PostgreTsTzRangeArrayType.ListType) {
            javaType = ((PostgreTsTzRangeArrayType.ListType<?>) type).elementType;
        } else {
            javaType = type.javaType.getComponentType();
        }
        assert !javaType.isArray();
        return new PostgreTsTzRangeType(javaType, rangeFunc);
    }

    public static final PostgreTsTzRangeType TEXT = new PostgreTsTzRangeType(String.class, null);


    /**
     * private constructor
     */
    private PostgreTsTzRangeType(final Class<?> javaType, final @Nullable RangeFunction<OffsetDateTime, ?> function) {
        super(javaType, OffsetDateTime.class, function, PostgreTsTzRangeType::parseOffsetDateTime);
    }


    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.TSTZRANGE;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        final PostgreTsTzRangeArrayType arrayType;
        final RangeFunction<OffsetDateTime, ?> rangeFunc = this.rangeFunc;
        if (rangeFunc == null) {
            assert this.javaType == String.class;
            arrayType = PostgreTsTzRangeArrayType.LINEAR;
        } else {
            arrayType = PostgreTsTzRangeArrayType.fromFunc(this.javaType, rangeFunc);
        }
        return arrayType;
    }


    @Override
    public MappingType subtype() {
        return OffsetDateTimeType.INSTANCE;
    }


    @Override
    void boundToText(OffsetDateTime bound, Consumer<String> appender) {
        appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
        appender.accept(bound.format(_TimeUtils.OFFSET_DATETIME_FORMATTER_6));
        appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
    }


    @Override
    Class<OffsetDateTime> boundJavaType() {
        return OffsetDateTime.class;
    }

    @Override
    <Z> PostgreSingleRangeType<OffsetDateTime> getInstanceFrom(Class<Z> javaType, RangeFunction<OffsetDateTime, Z> rangeFunc) {
        return fromFunc(javaType, rangeFunc);
    }

    @Nullable
    static OffsetDateTime parseOffsetDateTime(final String text) {
        final OffsetDateTime bound;
        if (INFINITY.equalsIgnoreCase(text)) {
            bound = null;
        } else {
            bound = OffsetDateTime.parse(text, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
        }
        return bound;
    }


}
