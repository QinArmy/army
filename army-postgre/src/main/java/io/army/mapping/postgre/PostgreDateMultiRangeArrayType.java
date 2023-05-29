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

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 * This class representing Postgre datemultirange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">datemultirange</a>
 */
public final class PostgreDateMultiRangeArrayType extends PostgreMultiRangeArrayType<LocalDate> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType's component class no 'create' static factory method.
     */
    public static PostgreDateMultiRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreDateMultiRangeArrayType instance;
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreDateMultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreDateMultiRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType two(or more) dimension array
     */
    public static PostgreDateMultiRangeArrayType fromFunc(final Class<?> javaType,
                                                          final RangeFunction<LocalDate, ?> function) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreDateMultiRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreDateMultiRangeArrayType(javaType, function);
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
    public static PostgreDateMultiRangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreDateMultiRangeArrayType.class, javaType);
        }

        return new PostgreDateMultiRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), LocalDate.class, methodName)
        );
    }

    /**
     * private constructor
     */
    private PostgreDateMultiRangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<LocalDate, ?> rangeFunc) {
        super(javaType, LocalDate.class, rangeFunc, LocalDate::parse);
    }


    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.DATEMULTIRANGE_ARRAY;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final RangeFunction<LocalDate, ?> rangeFunc = this.rangeFunc;
        final PostgreDateMultiRangeArrayType type;
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
        final RangeFunction<LocalDate, ?> rangeFunc = this.rangeFunc;
        final MappingType type;
        if (rangeFunc == null) {
            assert ArrayUtils.underlyingComponent(javaType) == String.class;
            if (javaType == String[][].class) {
                type = PostgreDateMultiRangeType.from(javaType.getComponentType());
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (ArrayUtils.dimensionOf(javaType) > 2) {
            type = fromFunc(javaType.getComponentType(), rangeFunc);
        } else {
            type = PostgreDateMultiRangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    void boundToText(LocalDate bound, Consumer<String> appender) {
        PostgreDateRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    Class<LocalDate> boundJavaType() {
        return LocalDate.class;
    }

    @Override
    MappingType compatibleFor(Class<?> targetType, RangeFunction<LocalDate, ?> rangeFunc)
            throws NoMatchMappingException {
        return fromFunc(targetType, rangeFunc);
    }

}
