package io.army.mapping.postgre;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

/**
 * <p>
 * Package class This class is base class of below:
 *     <ul>
 *         <li>{@link PostgreRangeType}</li>
 *         <li>{@link PostgreSingleRangeArrayType}</li>
 *         <li>{@link PostgreMultiRangeArrayType}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public abstract class _ArmyPostgreRangeType extends _ArmyNoInjectionMapping {


    protected final PostgreDataType sqlType;

    protected final Class<?> javaType;

    protected final RangeFunction<Object, ?> rangeFunc;

    protected final PostgreSingleRangeType.MockRangeFunction<?> mockFunction;

    /**
     * <p>
     * package constructor
     * </p>
     */
    @SuppressWarnings("unchecked")
    protected _ArmyPostgreRangeType(final PostgreDataType sqlType, final Class<?> javaType,
                                    final @Nullable RangeFunction<?, ?> rangeFunc) {
        final Class<?> underlyingType;
        if (javaType == String.class) {
            underlyingType = javaType;
        } else {
            underlyingType = ArrayUtils.underlyingComponent(javaType);
        }
        assert rangeFunc != null || underlyingType == String.class;
        this.sqlType = sqlType;
        this.javaType = javaType;
        this.rangeFunc = (RangeFunction<Object, ?>) rangeFunc;
        if (underlyingType == String.class || ArmyPostgreRange.class.isAssignableFrom(javaType)) {
            this.mockFunction = null;
        } else {
            this.mockFunction = PostgreRangeType.createMockFunction(javaType, boundJavaType(sqlType));
        }
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return this.sqlType;
    }

    @Override
    public final boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (this.getClass().isInstance(type)) {
            match = ((_ArmyPostgreRangeType) type).sqlType == this.sqlType;
        } else {
            match = false;
        }
        return match;
    }


    protected final void serialize(final Object bound, final Consumer<String> appender) {
        switch (this.sqlType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
            case INT4RANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY: {
                if (!(bound instanceof Integer)) {
                    throw boundTypeError(bound);
                }
                appender.accept(bound.toString());
            }
            break;
            case INT8RANGE:
            case INT8MULTIRANGE:
            case INT8RANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY: {
                if (!(bound instanceof Long)) {
                    throw boundTypeError(bound);
                }
                appender.accept(bound.toString());
            }
            break;
            case NUMRANGE:
            case NUMMULTIRANGE:
            case NUMRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY: {
                if (!(bound instanceof BigDecimal)) {
                    throw boundTypeError(bound);
                }
                appender.accept(((BigDecimal) bound).toPlainString());
            }
            break;
            case DATERANGE:
            case DATEMULTIRANGE:
            case DATERANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY: {
                if (!(bound instanceof LocalDate)) {
                    throw boundTypeError(bound);
                }
                appender.accept(bound.toString());
            }
            break;
            case TSRANGE:
            case TSMULTIRANGE:
            case TSRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY: {
                if (!(bound instanceof LocalDateTime)) {
                    throw boundTypeError(bound);
                }
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
                appender.accept(((LocalDateTime) bound).format(_TimeUtils.DATETIME_FORMATTER_6));
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
            }
            break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
            case TSTZRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY: {
                if (!(bound instanceof OffsetDateTime)) {
                    throw boundTypeError(bound);
                }
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
                appender.accept(((OffsetDateTime) bound).format(_TimeUtils.OFFSET_DATETIME_FORMATTER_6));
                appender.accept(String.valueOf(_Constant.DOUBLE_QUOTE));
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.sqlType);
        }

    }

    protected final Object deserialize(final String text) {
        final Object value;
        switch (this.sqlType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
            case INT4RANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY:
                value = Integer.parseInt(text);
                break;
            case INT8RANGE:
            case INT8MULTIRANGE:
            case INT8RANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
                value = Long.parseLong(text);
                break;
            case NUMRANGE:
            case NUMMULTIRANGE:
            case NUMRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
                value = new BigDecimal(text);
                break;
            case DATERANGE:
            case DATEMULTIRANGE:
            case DATERANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
                //TODO postgre date format?
                value = LocalDate.parse(text);
                break;
            case TSRANGE:
            case TSMULTIRANGE:
            case TSRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
                value = LocalDateTime.parse(text, _TimeUtils.DATETIME_FORMATTER_6);
                break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
            case TSTZRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:
                value = OffsetDateTime.parse(text, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.sqlType);
        }
        return value;
    }


    protected PostgreMultiRangeType fromMultiArray(PostgreMultiRangeArrayType type) {
        throw new UnsupportedOperationException();
    }

    protected PostgreSingleRangeType fromSingleArray(PostgreSingleRangeArrayType type) {
        throw new UnsupportedOperationException();
    }

    private MetaException boundTypeError(@Nullable Object bound) {
        String m = String.format("%s type return bound java type %s error.", this.javaType.getName(),
                _ClassUtils.safeClassName(bound));
        return new MetaException(m);
    }


    protected static Class<?> boundJavaType(final PostgreDataType sqlType) {
        final Class<?> type;
        switch (sqlType) {
            case INT4RANGE:
            case INT4MULTIRANGE:
            case INT4RANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY:
                type = Integer.class;
                break;
            case INT8RANGE:
            case INT8MULTIRANGE:
            case INT8RANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
                type = Long.class;
                break;
            case NUMRANGE:
            case NUMMULTIRANGE:
            case NUMRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
                type = BigDecimal.class;
                break;
            case DATERANGE:
            case DATEMULTIRANGE:
            case DATERANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
                type = LocalDate.class;
                break;
            case TSRANGE:
            case TSMULTIRANGE:
            case TSRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
                type = LocalDateTime.class;
                break;
            case TSTZRANGE:
            case TSTZMULTIRANGE:
            case TSTZRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:
                type = OffsetDateTime.class;
                break;
            default:
                throw _Exceptions.unexpectedEnum(sqlType);
        }
        return type;
    }

    @Nullable
    protected static RangeFunction<?, ?> tryCreateDefaultRangeFunc(final Class<?> targetType, final Class<?> elementType) {
        RangeFunction<?, ?> rangeFunc;
        try {
            rangeFunc = PostgreRangeType.createRangeFunction(targetType, elementType, CREATE);
        } catch (Throwable e) {
            rangeFunc = null;
        }
        return rangeFunc;
    }


}
