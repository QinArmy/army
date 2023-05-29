package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
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
 * This class representing Postgre int4multirange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4multirange</a>
 */
public class PostgreInt4MultiRangeType extends PostgreMultiRangeType<Integer> {

    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String}(or String[]) and no 'create' static factory method.
     */
    public static PostgreInt4MultiRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreInt4MultiRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else if (javaType == String[].class) {
            instance = new PostgreInt4MultiRangeType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreInt4MultiRangeType fromFunc(final Class<? extends R[]> javaType,
                                                         final RangeFunction<Integer, R> function) {
        if (!javaType.isArray() || javaType.getComponentType().isArray()) {
            throw errorJavaType(PostgreInt4MultiRangeType.class, javaType);
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
    public static PostgreInt4MultiRangeType fromMethod(final Class<?> javaType, final String methodName) {
        final Class<?> componentType;
        if (!javaType.isArray() || (componentType = javaType.getComponentType()).isArray()) {
            throw errorJavaType(PostgreInt4MultiRangeType.class, javaType);
        }
        return new PostgreInt4MultiRangeType(javaType, createRangeFunction(componentType, Integer.class, methodName));
    }

    public static <E> PostgreInt4MultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         RangeFunction<Integer, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreInt4MultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreInt4MultiRangeType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreInt4MultiRangeType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass, createRangeFunction(elementClass, Integer.class, methodName));
    }


    static PostgreInt4MultiRangeType fromArrayType(final PostgreInt4MultiRangeArrayType type) {
        final RangeFunction<Integer, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        final Class<?> javaType = type.javaType.getComponentType();
        assert !javaType.getComponentType().isArray();
        return new PostgreInt4MultiRangeType(javaType, rangeFunc);
    }

    private static PostgreInt4MultiRangeType fromFunc0(final Class<?> javaType,
                                                       final RangeFunction<Integer, ?> function) {
        return new PostgreInt4MultiRangeType(javaType, function);
    }


    public static final PostgreInt4MultiRangeType TEXT = new PostgreInt4MultiRangeType(String.class, null);

    /**
     * private constructor
     */
    private PostgreInt4MultiRangeType(Class<?> javaType, @Nullable RangeFunction<Integer, ?> rangeFunc) {
        super(javaType, Integer.class, rangeFunc, Integer::parseInt);
    }


    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4MULTIRANGE;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Integer, ?> rangFunc = this.rangeFunc;
        final PostgreInt4MultiRangeArrayType type;
        if (rangFunc == null) {
            assert javaType == String.class || javaType == String[].class;
            type = PostgreInt4MultiRangeArrayType.from(String[][].class);
        } else if (this instanceof ListType) {
            type = PostgreInt4MultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType), rangFunc);
        } else {
            type = PostgreInt4MultiRangeArrayType.fromFunc(ArrayUtils.arrayClassOf(javaType), rangFunc);
        }
        return type;
    }

    @Override
    public final MappingType subtype() {
        return IntegerType.INSTANCE;
    }


    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<Integer, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreInt4MultiRangeType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc0(targetType, rangeFunc);
        }
        return instance;
    }

    @Override
    final void boundToText(Integer bound, Consumer<String> appender) {
        appender.accept(bound.toString());
    }

    @Override
    final Class<Integer> boundJavaType() {
        return Integer.class;
    }

    private static final class ListType<E> extends PostgreInt4MultiRangeType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        private final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier, Class<E> elementType,
                         @Nullable RangeFunction<Integer, ?> rangeFunc) {
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
