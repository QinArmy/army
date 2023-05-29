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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>
 * This class representing Postgre tstzmultirange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">tstzmultirange</a>
 */
public class PostgreTsTzMultiRangeType extends PostgreMultiRangeType<OffsetDateTime> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String}(or String[]) and no 'create' static factory method.
     */
    public static PostgreTsTzMultiRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreTsTzMultiRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else if (javaType == String[].class) {
            instance = new PostgreTsTzMultiRangeType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreTsTzMultiRangeType fromFunc(final Class<? extends R[]> javaType,
                                                         final RangeFunction<OffsetDateTime, R> function) {
        if (!javaType.isArray() || javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreTsTzMultiRangeType.class, javaType);
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
    public static PostgreTsTzMultiRangeType fromMethod(final Class<?> javaType, final String methodName) {
        final Class<?> componentType;
        if (!javaType.isArray() || (componentType = javaType.getComponentType()).isArray()) {
            throw errorJavaType(PostgreTsTzMultiRangeType.class, javaType);
        }
        return new PostgreTsTzMultiRangeType(javaType, createRangeFunction(componentType, OffsetDateTime.class, methodName));
    }

    public static <E> PostgreTsTzMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         RangeFunction<OffsetDateTime, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsTzMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreTsTzMultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsTzMultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass, createRangeFunction(elementClass, OffsetDateTime.class, methodName));
    }


    static PostgreTsTzMultiRangeType fromArrayType(final PostgreTsTzMultiRangeArrayType type) {
        final RangeFunction<OffsetDateTime, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType.getComponentType();
        assert !javaType.getComponentType().isArray();
        return new PostgreTsTzMultiRangeType(javaType, rangeFunc);
    }

    private static PostgreTsTzMultiRangeType fromFunc0(final Class<?> javaType,
                                                       final RangeFunction<OffsetDateTime, ?> function) {
        return new PostgreTsTzMultiRangeType(javaType, function);
    }


    public static final PostgreTsTzMultiRangeType TEXT = new PostgreTsTzMultiRangeType(String.class, null);

    /**
     * private constructor
     */
    private PostgreTsTzMultiRangeType(Class<?> javaType, @Nullable RangeFunction<OffsetDateTime, ?> rangeFunc) {
        super(javaType, OffsetDateTime.class, rangeFunc, PostgreTsTzRangeType::parseOffsetDateTime);
    }


    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.TSTZMULTIRANGE;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        final RangeFunction<OffsetDateTime, ?> rangFunc = this.rangeFunc;
        final PostgreTsTzMultiRangeArrayType type;
        if (rangFunc == null) {
            assert javaType == String.class || javaType == String[].class;
            type = PostgreTsTzMultiRangeArrayType.from(String[][].class);
        } else if (this instanceof ListType) {
            type = PostgreTsTzMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType), rangFunc);
        } else {
            type = PostgreTsTzMultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(javaType), rangFunc);
        }
        return type;
    }

    @Override
    public final MappingType subtype() {
        return LocalDateTimeType.INSTANCE;
    }


    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<OffsetDateTime, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreTsTzMultiRangeType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc0(targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    final void boundToText(OffsetDateTime bound, Consumer<String> appender) {
        PostgreTsTzRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    final Class<OffsetDateTime> boundJavaType() {
        return OffsetDateTime.class;
    }

    private static final class ListType<E> extends PostgreTsTzMultiRangeType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        private final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier, Class<E> elementType,
                         @Nullable RangeFunction<OffsetDateTime, ?> rangeFunc) {
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
