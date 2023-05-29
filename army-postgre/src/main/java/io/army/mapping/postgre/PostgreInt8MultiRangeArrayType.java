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
 * This class representing Postgre int8multirange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int8multirange</a>
 */
public final class PostgreInt8MultiRangeArrayType extends PostgreMultiRangeArrayType<Long> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType's component class no 'create' static factory method.
     */
    public static PostgreInt8MultiRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreInt8MultiRangeArrayType instance;
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt8MultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreInt8MultiRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType two(or more) dimension array
     */
    public static PostgreInt8MultiRangeArrayType fromFunc(final Class<?> javaType,
                                                          final RangeFunction<Long, ?> function) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt8MultiRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreInt8MultiRangeArrayType(javaType, function);
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
    public static PostgreInt8MultiRangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt8MultiRangeArrayType.class, javaType);
        }

        return new PostgreInt8MultiRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), Long.class, methodName)
        );
    }

    /**
     * private constructor
     */
    private PostgreInt8MultiRangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<Long, ?> rangeFunc) {
        super(javaType, Long.class, rangeFunc, Long::parseLong);
    }


    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT8MULTIRANGE_ARRAY;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final RangeFunction<Long, ?> rangeFunc = this.rangeFunc;
        final PostgreInt8MultiRangeArrayType type;
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
        final RangeFunction<Long, ?> rangeFunc = this.rangeFunc;
        final MappingType type;
        if (rangeFunc == null) {
            if (javaType == String[][].class) {
                type = PostgreInt8MultiRangeType.from(javaType.getComponentType());
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (ArrayUtils.dimensionOf(javaType) > 2) {
            type = fromFunc(javaType.getComponentType(), rangeFunc);
        } else {
            type = PostgreInt8MultiRangeType.fromArrayType(this);
        }
        return type;
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
    MappingType compatibleFor(Class<?> targetType, RangeFunction<Long, ?> rangeFunc)
            throws NoMatchMappingException {
        return fromFunc(targetType, rangeFunc);
    }


}
