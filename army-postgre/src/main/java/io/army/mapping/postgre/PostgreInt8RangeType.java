package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 * This class representing Postgre int8range type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4range</a>
 */
public final class PostgreInt8RangeType extends PostgreSingleRangeType<Long> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String} and no 'create' static factory method.
     */
    public static PostgreInt8RangeType from(final Class<?> javaType) throws MetaException {
        final PostgreInt8RangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreInt8RangeType fromFunc(final Class<? extends R> javaType,
                                                    final RangeFunction<Long, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt8RangeType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreInt8RangeType(javaType, function);
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
    public static PostgreInt8RangeType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt8RangeType.class, javaType);
        }
        return new PostgreInt8RangeType(javaType, createRangeFunction(javaType, Long.class, methodName));
    }


    /**
     * package method
     */
    static PostgreInt8RangeType fromArrayType(final PostgreInt8RangeArrayType type) {
        final Class<?> javaType;
        final RangeFunction<Long, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        if (type instanceof PostgreInt8RangeArrayType.ListType) {
            javaType = ((PostgreInt8RangeArrayType.ListType<?>) type).elementType;
        } else {
            javaType = type.javaType.getComponentType();
        }
        assert !javaType.isArray();
        return new PostgreInt8RangeType(javaType, rangeFunc);
    }

    public static final PostgreInt8RangeType TEXT = new PostgreInt8RangeType(String.class, null);


    /**
     * private constructor
     */
    private PostgreInt8RangeType(final Class<?> javaType, final @Nullable RangeFunction<Long, ?> function) {
        super(javaType, Long.class, function, Long::parseLong);
    }


    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT8RANGE;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        final PostgreInt8RangeArrayType arrayType;
        final RangeFunction<Long, ?> rangeFunc = this.rangeFunc;
        if (rangeFunc == null) {
            assert this.javaType == String.class;
            arrayType = PostgreInt8RangeArrayType.LINEAR;
        } else {
            arrayType = PostgreInt8RangeArrayType.fromFunc(this.javaType, rangeFunc);
        }
        return arrayType;
    }


    @Override
    public MappingType subtype() {
        return LongType.INSTANCE;
    }


    @Override
    void boundToText(Long bound, Consumer<String> appender) {
        appender.accept(bound.toString());
    }


    @Override
    Class<Long> boundJavaType() {
        return Long.class;
    }

    @Override
    <Z> PostgreSingleRangeType<Long> getInstanceFrom(Class<Z> javaType, RangeFunction<Long, Z> rangeFunc) {
        return fromFunc(javaType, rangeFunc);
    }


}
