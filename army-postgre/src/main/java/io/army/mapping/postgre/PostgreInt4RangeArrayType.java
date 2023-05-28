package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.SingleGenericsMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

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


    public static PostgreInt4RangeArrayType from(final Class<?> javaType) {
        final PostgreInt4RangeArrayType instance;
        if (javaType == String[].class) {
            instance = TEXT;
        } else if (javaType.isArray() && _ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreInt4RangeArrayType(javaType, null);
        } else {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }
        return instance;
    }

    public static PostgreInt4RangeArrayType fromFunc(final Class<?> javaType,
                                                     final RangeFunction<Integer, ?> function) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }
        Objects.requireNonNull(function);
        return new PostgreInt4RangeArrayType(javaType, function);
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
                PostgreRangeType.createRangeFunction(_ArrayUtils.underlyingComponent(javaType), Integer.class, methodName)
        );
    }

    public static final PostgreInt4RangeArrayType TEXT = new PostgreInt4RangeArrayType(String.class, null);


    private PostgreInt4RangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<Integer, ?> rangeFunc) {
        super(javaType, Integer.TYPE, rangeFunc, Integer::parseInt);
    }


    @Override
    public final MappingType arrayTypeOfThis() {
        final RangeFunction<Integer, ?> rangeFunc = this.rangeFunc;
        final Class<?> javaType = this.javaType;
        final MappingType type;
        if (rangeFunc == null) {
            assert javaType == String.class;
            throw dontSupportArrayType(this);
        } else if (this instanceof ListType) {
            type = fromFunc(_ArrayUtils.arrayClassOf(((ListType<?>) this).elementType), rangeFunc);
        } else {
            type = fromFunc(_ArrayUtils.arrayClassOf(javaType), rangeFunc);
        }
        return type;
    }

    @Override
    public final MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Integer, ?> rangeFunc = this.rangeFunc;

        final int dimension;
        dimension = _ArrayUtils.dimensionOfType(this);
        final MappingType type;
        if (dimension > 1) {
            assert rangeFunc != null;
            type = fromFunc(javaType.getComponentType(), rangeFunc);
        } else if (rangeFunc == null) {
            assert javaType == String.class;
            type = PostgreInt4RangeType.TEXT;
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
    final void boundToText(Integer bound, Consumer<String> consumer) {
        consumer.accept(bound.toString());
    }

    private static final class ListType<E> extends PostgreInt4RangeArrayType
            implements SingleGenericsMapping.ListMapping<E> {

        private final Supplier<List<E>> supplier;

        private final Class<E> elementType;

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
