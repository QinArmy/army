package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.UnaryGenericsMapping;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>
 * This class representing Postgre int4range array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4range</a>
 */
public class PostgreInt4RangeArrayType extends PostgreSingleRangeArrayType<Integer> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't array of {@link String} and component class no 'create' static factory method.
     */
    public static PostgreInt4RangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreInt4RangeArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreInt4RangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreInt4RangeArrayType fromFunc(final Class<? extends R[]> javaType,
                                                         final RangeFunction<Integer, R> function) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return fromFunc0(javaType, function);
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
    public static PostgreInt4RangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }

        return new PostgreInt4RangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), Integer.class, methodName)
        );
    }

    /**
     * package method
     */
    static PostgreInt4RangeArrayType fromInt4Range(final PostgreInt4RangeType type) {
        final RangeFunction<Integer, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        return fromFunc0(ArrayUtils.arrayClassOf(type.javaType), rangeFunc);
    }

    private static PostgreInt4RangeArrayType fromFunc0(final Class<?> javaType,
                                                       final RangeFunction<Integer, ?> rangeFunc) {
        return new PostgreInt4RangeArrayType(javaType, rangeFunc);
    }

    /**
     * one dimension {@link String} array
     */
    public static final PostgreInt4RangeArrayType LINEAR = new PostgreInt4RangeArrayType(String[].class, null);

    /**
     * private constructor
     */
    private PostgreInt4RangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<Integer, ?> rangeFunc) {
        super(javaType, Integer.class, rangeFunc, Integer::parseInt);
    }


    @Override
    public final MappingType arrayTypeOfThis() {
        final RangeFunction<Integer, ?> rangeFunc = this.rangeFunc;
        final MappingType type;
        if (rangeFunc == null) {
            type = from(ArrayUtils.arrayClassOf(this.javaType));
        } else if (this instanceof ListType) {
            type = fromFunc0(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType, 2), rangeFunc);
        } else {
            type = fromFunc0(ArrayUtils.arrayClassOf(this.javaType), rangeFunc);
        }
        return type;
    }

    @Override
    public final MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Integer, ?> rangeFunc = this.rangeFunc;

        final MappingType type;
        final Class<?> componentType;
        if (rangeFunc == null) {
            if (javaType == String[].class) {
                type = PostgreInt4RangeType.TEXT;
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (this instanceof ListType) {
            type = PostgreInt4RangeType.fromArrayType(this);
        } else if ((componentType = javaType.getComponentType()).isArray()) {
            type = fromFunc0(componentType, rangeFunc);
        } else {
            type = PostgreInt4RangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4RANGE_ARRAY;
    }

    @Override
    final void boundToText(Integer bound, Consumer<String> appender) {
        appender.accept(bound.toString());
    }

    @Override
    final Class<Integer> boundJavaType() {
        return Integer.class;
    }

    static final class ListType<E> extends PostgreInt4RangeArrayType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        final Class<E> elementType;

        private ListType(Class<?> javaType, Supplier<List<E>> supplier,
                         Class<E> elementType, RangeFunction<Integer, ?> function) {
            super(javaType, function);
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
