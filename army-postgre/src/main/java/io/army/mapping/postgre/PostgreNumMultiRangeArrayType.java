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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * <p>
 * This class representing Postgre nummultirange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">nummultirange</a>
 */
public final class PostgreNumMultiRangeArrayType extends PostgreMultiRangeArrayType<BigDecimal> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType's component class no 'create' static factory method.
     */
    public static PostgreNumMultiRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreNumMultiRangeArrayType instance;
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreNumMultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreNumMultiRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType two(or more) dimension array
     */
    public static PostgreNumMultiRangeArrayType fromFunc(final Class<?> javaType,
                                                         final RangeFunction<BigDecimal, ?> function) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreNumMultiRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreNumMultiRangeArrayType(javaType, function);
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
    public static PostgreNumMultiRangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreNumMultiRangeArrayType.class, javaType);
        }

        return new PostgreNumMultiRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), BigDecimal.class, methodName)
        );
    }

    /**
     * private constructor
     */
    private PostgreNumMultiRangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<BigDecimal, ?> rangeFunc) {
        super(javaType, BigDecimal.class, rangeFunc, BigDecimal::new);
    }


    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.NUMMULTIRANGE_ARRAY;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final RangeFunction<BigDecimal, ?> rangeFunc = this.rangeFunc;
        final PostgreNumMultiRangeArrayType type;
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
        final RangeFunction<BigDecimal, ?> rangeFunc = this.rangeFunc;
        final MappingType type;
        if (rangeFunc == null) {
            assert ArrayUtils.underlyingComponent(javaType) == String.class;
            if (javaType == String[][].class) {
                type = PostgreNumMultiRangeType.from(javaType.getComponentType());
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (ArrayUtils.dimensionOf(javaType) > 2) {
            type = fromFunc(javaType.getComponentType(), rangeFunc);
        } else {
            type = PostgreNumMultiRangeType.fromArrayType(this);
        }
        return type;
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
    MappingType compatibleFor(Class<?> targetType, RangeFunction<BigDecimal, ?> rangeFunc)
            throws NoMatchMappingException {
        return fromFunc(targetType, rangeFunc);
    }


}
