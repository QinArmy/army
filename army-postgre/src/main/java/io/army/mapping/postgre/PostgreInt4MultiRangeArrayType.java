package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 * This class representing Postgre int4multirange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4multirange</a>
 */
public final class PostgreInt4MultiRangeArrayType extends PostgreMultiRangeArrayType<Integer> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType's component class no 'create' static factory method.
     */
    public static PostgreInt4MultiRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreInt4MultiRangeArrayType instance;
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt4MultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreInt4MultiRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType two(or more) dimension array
     */
    public static PostgreInt4MultiRangeArrayType fromFunc(final Class<?> javaType,
                                                          final RangeFunction<Integer, ?> function) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt4MultiRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreInt4MultiRangeArrayType(javaType, function);
    }


    /**
     * <p>
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
    public static PostgreInt4MultiRangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt4MultiRangeArrayType.class, javaType);
        }

        return new PostgreInt4MultiRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), Integer.class, methodName)
        );
    }

    /**
     * private constructor
     */
    private PostgreInt4MultiRangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<Integer, ?> rangeFunc) {
        super(javaType, Integer.class, rangeFunc, Integer::parseInt);
    }


    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4MULTIRANGE_ARRAY;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final RangeFunction<Integer, ?> rangeFunc = this.rangeFunc;
        final PostgreInt4MultiRangeArrayType type;
        if (rangeFunc == null) {
            type = from(ArrayUtils.arrayClassOf(this.javaType));
        } else {
            type = fromFunc(ArrayUtils.arrayClassOf(this.javaType), rangeFunc);
        }
        return type;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Integer, ?> rangeFunc = this.rangeFunc;
        final MappingType type;
        if (rangeFunc == null) {
            assert ArrayUtils.underlyingComponent(javaType) == String.class;
            if (javaType == String[][].class) {
                type = PostgreInt4MultiRangeType.from(javaType.getComponentType());
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (ArrayUtils.dimensionOf(javaType) > 2) {
            type = fromFunc(javaType.getComponentType(), rangeFunc);
        } else {
            type = PostgreInt4MultiRangeType.fromArrayType(this);
        }
        return type;
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
    MappingType compatibleFor(Class<?> targetType, RangeFunction<Integer, ?> rangeFunc)
            throws NoMatchMappingException {
        return fromFunc(targetType, rangeFunc);
    }


}
