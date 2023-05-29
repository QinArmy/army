package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
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
 * This class representing Postgre int8range array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int8range</a>
 */
public class PostgreInt8RangeArrayType extends PostgreSingleRangeArrayType<Long> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't array of {@link String} and component class no 'create' static factory method.
     */
    public static PostgreInt8RangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreInt8RangeArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreInt8RangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreInt8RangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static PostgreInt8RangeArrayType fromFunc(final Class<?> javaType,
                                                     final RangeFunction<Long, ?> function) {
        if (!javaType.isArray()) {
            throw errorJavaType(PostgreInt8RangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreInt8RangeArrayType(javaType, function);
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
    public static PostgreInt8RangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt8RangeArrayType.class, javaType);
        }

        return new PostgreInt8RangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), Long.class, methodName)
        );
    }

    public static <E> PostgreInt8RangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         RangeFunction<Long, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreInt8RangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreInt8RangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreInt8RangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass,
                PostgreRangeType.createRangeFunction(elementClass, Long.class, methodName)
        );
    }


    /**
     * one dimension {@link String} array
     */
    public static final PostgreInt8RangeArrayType LINEAR = new PostgreInt8RangeArrayType(String[].class, null);

    /**
     * private constructor
     */
    private PostgreInt8RangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<Long, ?> rangeFunc) {
        super(javaType, Long.class, rangeFunc, Long::parseLong);
    }


    @Override
    public final MappingType arrayTypeOfThis() {
        final RangeFunction<Long, ?> rangeFunc = this.rangeFunc;
        final MappingType type;
        if (rangeFunc == null) {
            type = from(ArrayUtils.arrayClassOf(this.javaType));
        } else if (this instanceof ListType) {
            type = fromFunc(ArrayUtils.arrayClassOf(((ListType<?>) this).elementType, 2), rangeFunc);
        } else {
            type = fromFunc(ArrayUtils.arrayClassOf(this.javaType), rangeFunc);
        }
        return type;
    }

    @Override
    public final MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Long, ?> rangeFunc = this.rangeFunc;

        final MappingType type;
        final Class<?> componentType;
        if (rangeFunc == null) {
            if (javaType == String[].class) {
                type = PostgreInt8RangeType.TEXT;
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (this instanceof ListType) {
            type = PostgreInt8RangeType.fromArrayType(this);
        } else if ((componentType = javaType.getComponentType()).isArray()) {
            type = fromFunc(componentType, rangeFunc);
        } else {
            type = PostgreInt8RangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT8RANGE_ARRAY;
    }

    @Override
    final void boundToText(Long bound, Consumer<String> appender) {
        appender.accept(bound.toString());
    }

    @Override
    final Class<Long> boundJavaType() {
        return Long.class;
    }

    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<Long, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreInt8RangeArrayType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc(targetType, rangeFunc);
        }
        return instance;
    }

    static final class ListType<E> extends PostgreInt8RangeArrayType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier,
                         Class<E> elementType, RangeFunction<Long, ?> function) {
            super(List.class, function);
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
