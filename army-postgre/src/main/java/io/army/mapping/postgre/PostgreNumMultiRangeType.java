package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.BigDecimalType;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.UnaryGenericsMapping;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>
 * This class representing Postgre nummultirange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">nummultirange</a>
 */
public class PostgreNumMultiRangeType extends PostgreMultiRangeType<BigDecimal> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String}(or String[]) and no 'create' static factory method.
     */
    public static PostgreNumMultiRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreNumMultiRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else if (javaType == String[].class) {
            instance = new PostgreNumMultiRangeType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreNumMultiRangeType fromFunc(final Class<? extends R[]> javaType,
                                                        final RangeFunction<BigDecimal, R> function) {
        if (!javaType.isArray() || javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreNumMultiRangeType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return fromFunc0(javaType, function);
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
    public static PostgreNumMultiRangeType fromMethod(final Class<?> javaType, final String methodName) {
        final Class<?> componentType;
        if (!javaType.isArray() || (componentType = javaType.getComponentType()).isArray()) {
            throw errorJavaType(PostgreNumMultiRangeType.class, javaType);
        }
        return new PostgreNumMultiRangeType(javaType, createRangeFunction(componentType, BigDecimal.class, methodName));
    }

    public static <E> PostgreNumMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                        RangeFunction<BigDecimal, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreNumMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreNumMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                        String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreNumMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass, createRangeFunction(elementClass, BigDecimal.class, methodName));
    }


    static PostgreNumMultiRangeType fromArrayType(final PostgreNumMultiRangeArrayType type) {
        final RangeFunction<BigDecimal, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType.getComponentType();
        assert !javaType.getComponentType().isArray();
        return new PostgreNumMultiRangeType(javaType, rangeFunc);
    }

    private static PostgreNumMultiRangeType fromFunc0(final Class<?> javaType,
                                                      final RangeFunction<BigDecimal, ?> function) {
        return new PostgreNumMultiRangeType(javaType, function);
    }


    public static final PostgreNumMultiRangeType TEXT = new PostgreNumMultiRangeType(String.class, null);

    /**
     * private constructor
     */
    private PostgreNumMultiRangeType(Class<?> javaType, @Nullable RangeFunction<BigDecimal, ?> rangeFunc) {
        super(javaType, BigDecimal.class, rangeFunc, BigDecimal::new);
    }


    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.NUMMULTIRANGE;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        final RangeFunction<BigDecimal, ?> rangFunc = this.rangeFunc;
        final PostgreNumMultiRangeArrayType type;
        if (rangFunc == null) {
            assert javaType == String.class || javaType == String[].class;
            type = PostgreNumMultiRangeArrayType.from(String[][].class);
        } else if (this instanceof ListType) {
            type = PostgreNumMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType), rangFunc);
        } else {
            type = PostgreNumMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(javaType), rangFunc);
        }
        return type;
    }

    @Override
    public final MappingType subtype() {
        return BigDecimalType.INSTANCE;
    }


    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<BigDecimal, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreNumMultiRangeType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc0(targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    final void boundToText(BigDecimal bound, Consumer<String> appender) {
        appender.accept(bound.toPlainString());
    }

    @Override
    final Class<BigDecimal> boundJavaType() {
        return BigDecimal.class;
    }

    private static final class ListType<E> extends PostgreNumMultiRangeType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        private final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier, Class<E> elementType,
                         @Nullable RangeFunction<BigDecimal, ?> rangeFunc) {
            super(List.class, rangeFunc);
            this.supplier = supplier;
            this.elementType = elementType;
        }

        @Override
        public Class<E> genericsType() {
            return this.elementType;
        }

        @Override
        public Supplier<List<E>> listConstructor() {
            return this.supplier;
        }


    }//ListType


}
