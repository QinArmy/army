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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>
 * This class representing Postgre tstzrange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">tstzrange</a>
 */
public class PostgreTsTzRangeArrayType extends PostgreSingleRangeArrayType<OffsetDateTime> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't array of {@link String} and component class no 'create' static factory method.
     */
    public static PostgreTsTzRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreTsTzRangeArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreTsTzRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreTsTzRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType java type of array of return value of rangeFunc
     */
    public static PostgreTsTzRangeArrayType fromFunc(Class<?> javaType, RangeFunction<OffsetDateTime, ?> function) {
        if (!javaType.isArray()) {
            throw errorJavaType(PostgreTsTzRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreTsTzRangeArrayType(javaType, function);
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
    public static PostgreTsTzRangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreTsTzRangeArrayType.class, javaType);
        }

        return new PostgreTsTzRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), OffsetDateTime.class,
                        methodName)
        );
    }

    public static <E> PostgreTsTzRangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         RangeFunction<OffsetDateTime, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsTzRangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreTsTzRangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsTzRangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass,
                PostgreRangeType.createRangeFunction(elementClass, OffsetDateTime.class, methodName)
        );
    }


    /**
     * one dimension {@link String} array
     */
    public static final PostgreTsTzRangeArrayType LINEAR = new PostgreTsTzRangeArrayType(String[].class, null);

    /**
     * private constructor
     */
    private PostgreTsTzRangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<OffsetDateTime, ?> rangeFunc) {
        super(javaType, OffsetDateTime.class, rangeFunc, PostgreTsTzRangeType::parseOffsetDateTime);
    }


    @Override
    public final MappingType arrayTypeOfThis() {
        final RangeFunction<OffsetDateTime, ?> rangeFunc = this.rangeFunc;
        final PostgreTsTzRangeArrayType type;
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
        final RangeFunction<OffsetDateTime, ?> rangeFunc = this.rangeFunc;

        final MappingType type;
        final Class<?> componentType;
        if (rangeFunc == null) {
            if (javaType == String[].class) {
                type = PostgreTsTzRangeType.TEXT;
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (this instanceof ListType) {
            type = PostgreTsTzRangeType.fromArrayType(this);
        } else if ((componentType = javaType.getComponentType()).isArray()) {
            type = fromFunc(componentType, rangeFunc);
        } else {
            type = PostgreTsTzRangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.TSTZRANGE_ARRAY;
    }

    @Override
    final void boundToText(OffsetDateTime bound, Consumer<String> appender) {
        PostgreTsTzRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    final Class<OffsetDateTime> boundJavaType() {
        return OffsetDateTime.class;
    }

    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<OffsetDateTime, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreTsTzRangeArrayType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc(targetType, rangeFunc);
        }
        return instance;
    }

    static final class ListType<E> extends PostgreTsTzRangeArrayType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier,
                         Class<E> elementType, RangeFunction<OffsetDateTime, ?> function) {
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
