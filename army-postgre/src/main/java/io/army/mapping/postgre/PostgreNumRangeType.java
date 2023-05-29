package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.BigDecimalType;
import io.army.mapping.MappingType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * <p>
 * This class representing Postgre numrange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">numrange</a>
 */
public final class PostgreNumRangeType extends PostgreSingleRangeType<BigDecimal> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String} and no 'create' static factory method.
     */
    public static PostgreNumRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreNumRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreNumRangeType fromFunc(final Class<? extends R> javaType,
                                                   final RangeFunction<BigDecimal, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreNumRangeType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreNumRangeType(javaType, function);
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
    public static PostgreNumRangeType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreNumRangeType.class, javaType);
        }
        return new PostgreNumRangeType(javaType, createRangeFunction(javaType, BigDecimal.class, methodName));
    }


    /**
     * package method
     */
    static PostgreNumRangeType fromArrayType(final PostgreNumRangeArrayType type) {
        final Class<?> javaType;
        final RangeFunction<BigDecimal, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        if (type instanceof PostgreNumRangeArrayType.ListType) {
            javaType = ((PostgreNumRangeArrayType.ListType<?>) type).elementType;
        } else {
            javaType = type.javaType.getComponentType();
        }
        assert !javaType.isArray();
        return new PostgreNumRangeType(javaType, rangeFunc);
    }

    public static final PostgreNumRangeType TEXT = new PostgreNumRangeType(String.class, null);


    /**
     * private constructor
     */
    private PostgreNumRangeType(final Class<?> javaType, final @Nullable RangeFunction<BigDecimal, ?> function) {
        super(javaType, BigDecimal.class, function, BigDecimal::new);
    }


    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.NUMRANGE;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        final PostgreNumRangeArrayType arrayType;
        final RangeFunction<BigDecimal, ?> rangeFunc = this.rangeFunc;
        if (rangeFunc == null) {
            assert this.javaType == String.class;
            arrayType = PostgreNumRangeArrayType.LINEAR;
        } else {
            arrayType = PostgreNumRangeArrayType.fromFunc(this.javaType, rangeFunc);
        }
        return arrayType;
    }


    @Override
    public MappingType subtype() {
        return BigDecimalType.INSTANCE;
    }


    @Override
    void boundToText(BigDecimal bound, Consumer<String> appender) {
        appender.accept(bound.toPlainString());
    }


    @Override
    Class<BigDecimal> boundJavaType() {
        return BigDecimal.class;
    }

    @Override
    <Z> PostgreSingleRangeType<BigDecimal> getInstanceFrom(Class<Z> javaType, RangeFunction<BigDecimal, Z> rangeFunc) {
        return fromFunc(javaType, rangeFunc);
    }


}
