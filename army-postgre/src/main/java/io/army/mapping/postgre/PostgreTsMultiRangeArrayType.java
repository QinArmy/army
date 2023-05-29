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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * <p>
 * This class representing Postgre tsmultirange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">tsmultirange</a>
 */
public final class PostgreTsMultiRangeArrayType extends PostgreMultiRangeArrayType<LocalDateTime> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType's component class no 'create' static factory method.
     */
    public static PostgreTsMultiRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreTsMultiRangeArrayType instance;
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreTsMultiRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreTsMultiRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType two(or more) dimension array
     */
    public static PostgreTsMultiRangeArrayType fromFunc(final Class<?> javaType,
                                                        final RangeFunction<LocalDateTime, ?> function) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreTsMultiRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreTsMultiRangeArrayType(javaType, function);
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
    public static PostgreTsMultiRangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (!javaType.isArray() || !javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreTsMultiRangeArrayType.class, javaType);
        }

        return new PostgreTsMultiRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), LocalDateTime.class, methodName)
        );
    }

    /**
     * private constructor
     */
    private PostgreTsMultiRangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<LocalDateTime, ?> rangeFunc) {
        super(javaType, LocalDateTime.class, rangeFunc, PostgreTsRangeType::parseDateTime);
    }


    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.TSMULTIRANGE_ARRAY;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final RangeFunction<LocalDateTime, ?> rangeFunc = this.rangeFunc;
        final PostgreTsMultiRangeArrayType type;
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
        final RangeFunction<LocalDateTime, ?> rangeFunc = this.rangeFunc;
        final MappingType type;
        if (rangeFunc == null) {
            assert ArrayUtils.underlyingComponent(javaType) == String.class;
            if (javaType == String[][].class) {
                type = PostgreTsMultiRangeType.from(javaType.getComponentType());
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (ArrayUtils.dimensionOf(javaType) > 2) {
            type = fromFunc(javaType.getComponentType(), rangeFunc);
        } else {
            type = PostgreTsMultiRangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    void boundToText(LocalDateTime bound, Consumer<String> appender) {
        PostgreTsRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    Class<LocalDateTime> boundJavaType() {
        return LocalDateTime.class;
    }

    @Override
    MappingType compatibleFor(Class<?> targetType, RangeFunction<LocalDateTime, ?> rangeFunc)
            throws NoMatchMappingException {
        return fromFunc(targetType, rangeFunc);
    }


}
