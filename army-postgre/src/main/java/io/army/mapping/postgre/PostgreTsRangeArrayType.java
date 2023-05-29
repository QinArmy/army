package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing Postgre tsrange array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">tsrange</a>
 */
public class PostgreTsRangeArrayType extends PostgreSingleRangeArrayType<LocalDateTime> {

    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't array of {@link String} and component class no 'create' static factory method.
     */
    public static PostgreTsRangeArrayType from(final Class<?> javaType) throws MetaException {
        final PostgreTsRangeArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (!javaType.isArray()) {
            throw errorJavaType(PostgreTsRangeArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreTsRangeArrayType(javaType, null);
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    /**
     * @param javaType java type of array of return value of rangeFunc
     */
    public static PostgreTsRangeArrayType fromFunc(Class<?> javaType, RangeFunction<LocalDateTime, ?> rangeFunc) {
        if (!javaType.isArray()) {
            throw errorJavaType(PostgreTsRangeArrayType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(rangeFunc);
        return new PostgreTsRangeArrayType(javaType, rangeFunc);
    }

    public static PostgreTsRangeArrayType fromMethod(Class<?> javaType, String methodName) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }

        return new PostgreTsRangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(ArrayUtils.underlyingComponent(javaType),
                        LocalDateTime.class, methodName)
        );
    }

    public static <E> PostgreTsRangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                       RangeFunction<LocalDateTime, E> function) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsRangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        Objects.requireNonNull(function);
        return new ListType<>(supplier, elementClass, function);
    }

    public static <E> PostgreTsRangeArrayType fromList(Supplier<List<E>> supplier, Class<E> elementClass,
                                                       String methodName) {
        if (elementClass.isArray()) {
            throw errorJavaType(PostgreTsRangeArrayType.class, elementClass);
        }
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(elementClass);
        return new ListType<>(supplier, elementClass,
                PostgreRangeType.createRangeFunction(elementClass, LocalDateTime.class, methodName)
        );
    }


    /**
     * one dimension {@link String} array
     */
    public static final PostgreTsRangeArrayType LINEAR = new PostgreTsRangeArrayType(String[].class, null);


    /**
     * private constructor
     */
    private PostgreTsRangeArrayType(Class<?> javaType, @Nullable RangeFunction<LocalDateTime, ?> rangeFunc) {
        super(javaType, LocalDateTime.class, rangeFunc, PostgreTsRangeType::parseDateTime);
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.TSRANGE_ARRAY;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final RangeFunction<LocalDateTime, ?> rangeFunc = this.rangeFunc;
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
        final RangeFunction<LocalDateTime, ?> rangeFunc = this.rangeFunc;
        final Class<?> javaType = this.javaType, componentType;
        final MappingType type;
        if (rangeFunc == null) {
            if (javaType == String[].class) {
                type = PostgreTsRangeType.TEXT;
            } else {
                type = from(javaType.getComponentType());
            }
        } else if (this instanceof ListType) {
            type = PostgreTsRangeType.fromArrayType(this);
        } else if ((componentType = this.javaType.getComponentType()).isArray()) {
            type = fromFunc(componentType, rangeFunc);
        } else {
            type = PostgreTsRangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    final void boundToText(LocalDateTime bound, Consumer<String> appender) {
        PostgreTsRangeType.TEXT.boundToText(bound, appender);
    }

    @Override
    final Class<LocalDateTime> boundJavaType() {
        return LocalDateTime.class;
    }

    @Override
    final MappingType compatibleFor(Class<?> targetType, Class<?> elementType, RangeFunction<LocalDateTime, ?> rangeFunc)
            throws NoMatchMappingException {
        final PostgreTsRangeArrayType instance;
        if (targetType == List.class) {
            instance = new ListType<>(ArrayList::new, elementType, rangeFunc);
        } else {
            instance = fromFunc(targetType, rangeFunc);
        }
        return instance;
    }


    static final class ListType<E> extends PostgreTsRangeArrayType
            implements UnaryGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        final Class<E> elementType;

        private ListType(Supplier<List<E>> supplier,
                         Class<E> elementType, RangeFunction<LocalDateTime, ?> function) {
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
