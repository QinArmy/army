package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

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
        javaType = type.javaType.getComponentType();
        assert !javaType.isArray();
        final RangeFunction<Integer, ?> function = type.rangeFunc;
        assert function != null;
        return new PostgreInt4RangeType(javaType, function);
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
        final Class<?> javaType = this.javaType;
        final RangeFunction<Integer, ?> rangeFunc = this.rangeFunc;
        final MappingType arrayType;
        if (rangeFunc == null) {
            assert javaType == String.class;
            arrayType = PostgreInt4RangeArrayType.TEXT;
        } else {
            arrayType = PostgreInt4RangeArrayType.fromFunc(_ArrayUtils.arrayClassOf(javaType), rangeFunc);
        }
        return arrayType;
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        final RangeFunction<Integer, Z> rangeFunc;
        rangeFunc = PostgreRangeType.tryCreateDefaultRangeFunc(targetType, Integer.class);
        if (rangeFunc == null) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return fromFunc(targetType, rangeFunc);
    }

    @Override
    public MappingType subtype() {
        return IntegerType.INSTANCE;
    }


    @Override
    void boundToText(Integer bound, Consumer<String> consumer) {
        consumer.accept(bound.toString());
    }


}
