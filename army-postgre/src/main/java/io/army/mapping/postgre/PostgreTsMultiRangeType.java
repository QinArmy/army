package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.LocalDateTimeType;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.UnaryGenericsMapping;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing Postgre tsmultirange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">tsmultirange</a>
 */
public class PostgreTsMultiRangeType extends PostgreMultiRangeType<LocalDateTime> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String}(or String[]) and no 'create' static factory method.
     */
    public static PostgreTsMultiRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreTsMultiRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else if (javaType == String[].class) {
            instance = new PostgreTsMultiRangeType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreTsMultiRangeType fromFunc(final Class<? extends R[]> javaType,
                                                       final RangeFunction<LocalDateTime, R> function) {
        if (!javaType.isArray() || javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreTsMultiRangeType.class, javaType);
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
    public static PostgreTsMultiRangeType fromMethod(final Class<?> javaType, final String methodName) {
        final Class<?> componentType;
        if (!javaType.isArray() || (componentType = javaType.getComponentType()).isArray()) {
            throw errorJavaType(PostgreTsMultiRangeType.class, javaType);
        }
        return new PostgreTsMultiRangeType(javaType, createRangeFunction(componentType, LocalDateTime.class, methodName));
    }

    public static <E> PostgreTsMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                       RangeFunction<LocalDateTime, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreTsMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                       String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass, createRangeFunction(elementClass, LocalDateTime.class, methodName));
    }


    static PostgreTsMultiRangeType fromArrayType(final PostgreTsMultiRangeArrayType type) {
        final RangeFunction<LocalDateTime, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType.getComponentType();
        assert !javaType.getComponentType().isArray();
        return new PostgreTsMultiRangeType(javaType, rangeFunc);
    }

    private static PostgreTsMultiRangeType fromFunc0(final Class<?> javaType,
                                                     final RangeFunction<LocalDateTime, ?> function) {
        return new PostgreTsMultiRangeType(javaType, function);
    }


    public static final PostgreTsMultiRangeType TEXT = new PostgreTsMultiRangeType(String.class, null);

    /**
     * private constructor
     */
    private PostgreTsMultiRangeType(Class<?> javaType, @Nullable RangeFunction<LocalDateTime, ?> rangeFunc) {
        super(javaType, LocalDateTime.class, rangeFunc, PostgreTsRangeType::parseDateTime);
    }


    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.TSMULTIRANGE;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        final RangeFunction<LocalDateTime, ?> rangFunc = this.rangeFunc;
        final PostgreTsMultiRangeArrayType type;
        if (rangFunc == null) {
            assert javaType == String.class || javaType == String[].class;
            type = PostgreTsMultiRangeArrayType.from(String[][].class);
        } else if (this instanceof ListType) {
            type = PostgreTsMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType), rangFunc);
        } else {
            type = PostgreTsMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(javaType), rangFunc);
        }
        return type;
    }

    @Override
    public final MappingType subtype() {
        return LocalDateTimeType.INSTANCE;
    }


    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<LocalDateTime, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreTsMultiRangeType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc0(targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    final void boundToText(LocalDateTime bound, Consumer<String> appender) {
        PostgreTsRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    final Class<LocalDateTime> boundJavaType() {
        return LocalDateTime.class;
    }

    private static final class ListType<E> extends PostgreTsMultiRangeType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        private final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier, Class<E> elementType,
                         @Nullable RangeFunction<LocalDateTime, ?> rangeFunc) {
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
