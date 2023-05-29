package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * <p>
 * This class representing Postgre int4range type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4range</a>
 */
public final class PostgreInt4RangeType extends PostgreSingleRangeType<Integer> {

    public static PostgreInt4RangeType from(final Class<?> javaType) throws MetaException {
        final PostgreInt4RangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreInt4RangeType fromFunc(final Class<? extends R> javaType,
                                                    final RangeFunction<Integer, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreInt4RangeType(javaType, function);
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
    public static PostgreInt4RangeType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeType.class, javaType);
        }
        return new PostgreInt4RangeType(javaType, createRangeFunction(javaType, Integer.class, methodName));
    }


    /**
     * package method
     */
    static PostgreInt4RangeType fromArrayType(final PostgreInt4RangeArrayType type) {
        final Class<?> javaType;
        final RangeFunction<Integer, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        if (type instanceof PostgreInt4RangeArrayType.ListType) {
            javaType = ((PostgreInt4RangeArrayType.ListType<?>) type).elementType;
        } else {
            javaType = type.javaType.getComponentType();
        }
        assert !javaType.isArray();
        return new PostgreInt4RangeType(javaType, rangeFunc);
    }

    public static final PostgreInt4RangeType TEXT = new PostgreInt4RangeType(String.class, null);


    /**
     * private constructor
     */
    private PostgreInt4RangeType(final Class<?> javaType, final @Nullable RangeFunction<Integer, ?> function) {
        super(javaType, Integer.class, function, Integer::parseInt);
    }


    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4RANGE;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        final MappingType arrayType;
        if (this.rangeFunc == null) {
            assert this.javaType == String.class;
            arrayType = PostgreInt4RangeArrayType.LINEAR;
        } else {
            arrayType = PostgreInt4RangeArrayType.fromInt4Range(this);
        }
        return arrayType;
    }


    @Override
    public MappingType subtype() {
        return IntegerType.INSTANCE;
    }


    @Override
    void boundToText(Integer bound, Consumer<String> appender) {
        appender.accept(bound.toString());
    }


    @Override
    Class<Integer> boundJavaType() {
        return Integer.class;
    }

    @Override
    <Z> PostgreSingleRangeType<Integer> getInstanceFrom(Class<Z> javaType, RangeFunction<Integer, Z> rangeFunc) {
        return fromFunc(javaType, rangeFunc);
    }


}
