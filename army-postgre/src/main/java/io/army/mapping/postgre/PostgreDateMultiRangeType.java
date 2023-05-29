package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.LocalDateType;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.UnaryGenericsMapping;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>
 * This class representing Postgre datemultirange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">datemultirange</a>
 */
public class PostgreDateMultiRangeType extends PostgreMultiRangeType<LocalDate> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String}(or String[]) and no 'create' static factory method.
     */
    public static PostgreDateMultiRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreDateMultiRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else if (javaType == String[].class) {
            instance = new PostgreDateMultiRangeType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreDateMultiRangeType fromFunc(final Class<? extends R[]> javaType,
                                                         final RangeFunction<LocalDate, R> function) {
        if (!javaType.isArray() || javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreDateMultiRangeType.class, javaType);
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
    public static PostgreDateMultiRangeType fromMethod(final Class<?> javaType, final String methodName) {
        final Class<?> componentType;
        if (!javaType.isArray() || (componentType = javaType.getComponentType()).isArray()) {
            throw errorJavaType(PostgreDateMultiRangeType.class, javaType);
        }
        return new PostgreDateMultiRangeType(javaType, createRangeFunction(componentType, LocalDate.class, methodName));
    }

    public static <E> PostgreDateMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         RangeFunction<LocalDate, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreDateMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreDateMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreDateMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass, createRangeFunction(elementClass, LocalDate.class, methodName));
    }


    static PostgreDateMultiRangeType fromArrayType(final PostgreDateMultiRangeArrayType type) {
        final RangeFunction<LocalDate, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType.getComponentType();
        assert !javaType.getComponentType().isArray();
        return new PostgreDateMultiRangeType(javaType, rangeFunc);
    }

    static PostgreDateMultiRangeType fromSingleType(final PostgreDateRangeType type) {
        final RangeFunction<LocalDate, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType;
        assert !javaType.isArray();
        return new PostgreDateMultiRangeType(ArrayUtils.arrayClassOf(javaType), rangeFunc);
    }

    private static PostgreDateMultiRangeType fromFunc0(final Class<?> javaType,
                                                       final RangeFunction<LocalDate, ?> function) {
        return new PostgreDateMultiRangeType(javaType, function);
    }


    public static final PostgreDateMultiRangeType TEXT = new PostgreDateMultiRangeType(String.class, null);

    /**
     * private constructor
     */
    private PostgreDateMultiRangeType(Class<?> javaType, @Nullable RangeFunction<LocalDate, ?> rangeFunc) {
        super(javaType, LocalDate.class, rangeFunc, LocalDate::parse);
    }


    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.DATEMULTIRANGE;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        final RangeFunction<LocalDate, ?> rangFunc = this.rangeFunc;
        final PostgreDateMultiRangeArrayType type;
        if (rangFunc == null) {
            assert javaType == String.class || javaType == String[].class;
            type = PostgreDateMultiRangeArrayType.from(String[][].class);
        } else if (this instanceof ListType) {
            type = PostgreDateMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType), rangFunc);
        } else {
            type = PostgreDateMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(javaType), rangFunc);
        }
        return type;
    }

    @Override
    public final MappingType subtype() {
        return LocalDateType.INSTANCE;
    }

    @Override
    public final MappingType rangeType() {
        final RangeFunction<LocalDate, ?> rangeFunc = this.rangeFunc;
        final PostgreDateRangeType type;
        if (rangeFunc == null) {
            type = PostgreDateRangeType.TEXT;
        } else {
            type = PostgreDateRangeType.fromMultiType(this);
        }
        return type;
    }

    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<LocalDate, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreDateMultiRangeType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc0(targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    final void boundToText(LocalDate bound, Consumer<String> appender) {
        PostgreDateRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    final Class<LocalDate> boundJavaType() {
        return LocalDate.class;
    }

    static final class ListType<E> extends PostgreDateMultiRangeType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier, Class<E> elementType,
                         @Nullable RangeFunction<LocalDate, ?> rangeFunc) {
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
