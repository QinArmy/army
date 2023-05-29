package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.LocalDateType;
import io.army.mapping.MappingType;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * <p>
 * This class representing Postgre daterange type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">daterange</a>
 */
public final class PostgreDateRangeType extends PostgreSingleRangeType<LocalDate> {


    /**
     * @param javaType array class
     * @throws MetaException when javaType isn't  {@link String} and no 'create' static factory method.
     */
    public static PostgreDateRangeType from(final Class<?> javaType) throws MetaException {
        final PostgreDateRangeType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = fromMethod(javaType, CREATE);
        }
        return instance;
    }

    public static <R> PostgreDateRangeType fromFunc(final Class<? extends R> javaType,
                                                    final RangeFunction<LocalDate, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreDateRangeType.class, javaType);
        }
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(function);
        return new PostgreDateRangeType(javaType, function);
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
    public static PostgreDateRangeType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreDateRangeType.class, javaType);
        }
        return new PostgreDateRangeType(javaType, createRangeFunction(javaType, LocalDate.class, methodName));
    }


    /**
     * package method
     */
    static PostgreDateRangeType fromArrayType(final PostgreDateRangeArrayType type) {
        final Class<?> javaType;
        final RangeFunction<LocalDate, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        if (type instanceof PostgreDateRangeArrayType.ListType) {
            javaType = ((PostgreDateRangeArrayType.ListType<?>) type).elementType;
        } else {
            javaType = type.javaType.getComponentType();
        }
        assert !javaType.isArray();
        return new PostgreDateRangeType(javaType, rangeFunc);
    }

    static PostgreDateRangeType fromMultiType(final PostgreDateMultiRangeType type) {
        final Class<?> javaType;
        if (type instanceof PostgreDateMultiRangeType.ListType) {
            javaType = ((PostgreDateMultiRangeType.ListType<?>) type).elementType;
        } else {
            javaType = type.javaType.getComponentType();
        }
        assert !javaType.isArray();
        final RangeFunction<LocalDate, ?> rangeFunc = type.rangeFunc;
        assert rangeFunc != null;
        return new PostgreDateRangeType(javaType, rangeFunc);
    }

    public static final PostgreDateRangeType TEXT = new PostgreDateRangeType(String.class, null);


    /**
     * private constructor
     */
    private PostgreDateRangeType(final Class<?> javaType, final @Nullable RangeFunction<LocalDate, ?> function) {
        super(javaType, LocalDate.class, function, LocalDate::parse);
    }


    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.DATERANGE;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        final PostgreDateRangeArrayType arrayType;
        final RangeFunction<LocalDate, ?> rangeFunc = this.rangeFunc;
        if (rangeFunc == null) {
            assert this.javaType == String.class;
            arrayType = PostgreDateRangeArrayType.LINEAR;
        } else {
            arrayType = PostgreDateRangeArrayType.fromFunc(this.javaType, rangeFunc);
        }
        return arrayType;
    }


    @Override
    public MappingType subtype() {
        return LocalDateType.INSTANCE;
    }

    @Override
    public MappingType multiRangeType() {
        final RangeFunction<LocalDate, ?> rangeFunc = this.rangeFunc;
        final PostgreDateMultiRangeType type;
        if (rangeFunc == null) {
            type = PostgreDateMultiRangeType.TEXT;
        } else {
            type = PostgreDateMultiRangeType.fromSingleType(this);
        }
        return type;
    }

    @Override
    void boundToText(LocalDate bound, Consumer<String> appender) {
        appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
        appender.accept(bound.toString());
        appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
    }


    @Override
    Class<LocalDate> boundJavaType() {
        return LocalDate.class;
    }

    @Override
    <Z> PostgreSingleRangeType<LocalDate> getInstanceFrom(Class<Z> javaType, RangeFunction<LocalDate, Z> rangeFunc) {
        return fromFunc(javaType, rangeFunc);
    }


}
