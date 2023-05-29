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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>
 * This class representing Postgre daterange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">daterange</a>
 */
public class PostgreDateRangeArrayType extends PostgreSingleRangeArrayType<LocalDate> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't array of {@link String} and component class no 'create' static factory method.
     */
    public static PostgreDateRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreDateRangeArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreDateRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreDateRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType java type of array of return value of rangeFunc
     */
    public static PostgreDateRangeArrayType fromFunc(final Class<?> javaType,
                                                     final RangeFunction<LocalDate, ?> function) {
        if (!javaType.isArray()) {
            throw errorJavaType(PostgreDateRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreDateRangeArrayType(javaType, function);
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
    public static PostgreDateRangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreDateRangeArrayType.class, javaType);
        }

        return new PostgreDateRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType), LocalDate.class, methodName)
        );
    }

    public static <E> PostgreDateRangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         RangeFunction<LocalDate, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreDateRangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreDateRangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                         String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreDateRangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass,
                PostgreRangeType.createRangeFunction(elementClass, LocalDate.class, methodName)
        );
    }


    /**
     * one dimension {@link String} array
     */
    public static final PostgreDateRangeArrayType LINEAR = new PostgreDateRangeArrayType(String[].class, null);

    /**
     * private constructor
     */
    private PostgreDateRangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<LocalDate, ?> rangeFunc) {
        super(javaType, LocalDate.class, rangeFunc, LocalDate::parse);
    }


    @Override
    public final MappingType arrayTypeOfThis() {
        final RangeFunction<LocalDate, ?> rangeFunc = this.rangeFunc;
        final PostgreDateRangeArrayType type;
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
        final RangeFunction<LocalDate, ?> rangeFunc = this.rangeFunc;

        final MappingType type;
        final Class<?> componentType;
        if (rangeFunc == null) {
            if (javaType == String[].class) {
                type = PostgreDateRangeType.TEXT;
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (this instanceof ListType) {
            type = PostgreDateRangeType.fromArrayType(this);
        } else if ((componentType = javaType.getComponentType()).isArray()) {
            type = fromFunc(componentType, rangeFunc);
        } else {
            type = PostgreDateRangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.DATERANGE_ARRAY;
    }

    @Override
    final void boundToText(LocalDate bound, Consumer<String> appender) {
        PostgreDateRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    final Class<LocalDate> boundJavaType() {
        return LocalDate.class;
    }

    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<LocalDate, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreDateRangeArrayType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc(targetType, rangeFunc);
        }
        return instance;
    }

    static final class ListType<E> extends PostgreDateRangeArrayType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier,
                         Class<E> elementType, RangeFunction<LocalDate, ?> function) {
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
