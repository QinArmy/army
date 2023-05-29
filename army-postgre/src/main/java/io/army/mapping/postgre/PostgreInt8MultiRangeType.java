package io.army.mapping.postgre;


import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.UnaryGenericsMapping;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing Postgre int8multirange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int8multirange</a>
 */
public class PostgreInt8MultiRangeType extends PostgreMultiRangeType<Long> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String} and no 'create' static factory method.
     */
    public static PostgreInt8MultiRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreInt8MultiRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else if (javaType == String[].class) {
            instance = new PostgreInt8MultiRangeType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreInt8MultiRangeType fromFunc(final Class<? extends R[]> javaType,
                                                         final RangeFunction<Long, R> function) {
        if (!javaType.isArray() || javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt8MultiRangeType.class, javaType);
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
    public static PostgreInt8MultiRangeType fromMethod(final Class<?> javaType, final String methodName) {
        final Class<?> componentType;
        if (!javaType.isArray() || (componentType = javaType.getComponentType()).isArray()) {
            throw errorJavaType(PostgreInt8MultiRangeType.class, javaType);
        }
        return new PostgreInt8MultiRangeType(javaType, createRangeFunction(componentType, Long.class, methodName));
    }

    public static <E> PostgreInt8MultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         RangeFunction<Long, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreInt8MultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreInt8MultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreInt8MultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass, createRangeFunction(elementClass, Long.class, methodName));
    }


    static PostgreInt8MultiRangeType fromArrayType(final PostgreInt8MultiRangeArrayType type) {
        final RangeFunction<Long, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType.getComponentType();
        assert !javaType.getComponentType().isArray();
        return new PostgreInt8MultiRangeType(javaType, rangeFunc);
    }

    static PostgreInt8MultiRangeType fromSingleType(final PostgreInt8RangeType type) {
        final RangeFunction<Long, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType;
        assert !javaType.isArray();
        return new PostgreInt8MultiRangeType(ArrayUtils.arrayClassOf(javaType), rangeFunc);
    }

    private static PostgreInt8MultiRangeType fromFunc0(final Class<?> javaType,
                                                       final RangeFunction<Long, ?> function) {
        return new PostgreInt8MultiRangeType(javaType, function);
    }


    public static final PostgreInt8MultiRangeType TEXT = new PostgreInt8MultiRangeType(String.class, null);

    /**
     * private constructor
     */
    private PostgreInt8MultiRangeType(Class<?> javaType, @Nullable RangeFunction<Long, ?> rangeFunc) {
        super(javaType, Long.class, rangeFunc, Long::parseLong);
    }


    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT8MULTIRANGE;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Long, ?> rangFunc = this.rangeFunc;
        final PostgreInt8MultiRangeArrayType type;
        if (rangFunc == null) {
            assert javaType == String.class || javaType == String[].class;
            type = PostgreInt8MultiRangeArrayType.from(String[][].class);
        } else if (this instanceof ListType) {
            type = PostgreInt8MultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType), rangFunc);
        } else {
            type = PostgreInt8MultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(javaType), rangFunc);
        }
        return type;
    }

    @Override
    public final MappingType subtype() {
        return LongType.INSTANCE;
    }

    @Override
    public final MappingType rangeType() {
        final RangeFunction<Long, ?> rangeFunc = this.rangeFunc;
        final PostgreInt8RangeType type;
        if (rangeFunc == null) {
            type = PostgreInt8RangeType.TEXT;
        } else {
            type = PostgreInt8RangeType.fromMultiType(this);
        }
        return type;
    }


    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<Long, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreInt8MultiRangeType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc0(targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    final void boundToText(Long bound, Consumer<String> appender) {
        appender.accept(bound.toString());
    }

    @Override
    final Class<Long> boundJavaType() {
        return Long.class;
    }

    static final class ListType<E> extends PostgreInt8MultiRangeType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier, Class<E> elementType,
                         @Nullable RangeFunction<Long, ?> rangeFunc) {
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
