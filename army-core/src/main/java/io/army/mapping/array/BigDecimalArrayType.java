package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @see io.army.mapping.BigDecimalType
 * @since 1.0
 */
public class BigDecimalArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static BigDecimalArrayType from(final Class<?> javaType) {
        final BigDecimalArrayType instance;

        if (javaType == BigDecimal[].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(BigDecimalArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == BigDecimal.class) {
            instance = new BigDecimalArrayType(javaType);
        } else {
            throw errorJavaType(BigDecimalArrayType.class, javaType);
        }
        return instance;
    }

    public static final BigDecimalArrayType UNLIMITED = new BigDecimalArrayType(Object.class);

    public static final BigDecimalArrayType LINEAR = new BigDecimalArrayType(BigDecimal[].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private BigDecimalArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.DECIMAL_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public final <Z> MappingType compatibleFor(final Class<Z> targetType) throws NoMatchMappingException {
        if (targetType != String.class) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return StringType.INSTANCE;
    }


    @Override
    public final MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == BigDecimal[].class) {
            instance = BigDecimalType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return from(ArrayUtils.arrayClassOf(this.javaType));
    }

    @Override
    public final Object convert(MappingEnv env, final Object nonNull) throws CriteriaException {
        final Object value;
        if (this.javaType.isInstance(nonNull)) {
            value = nonNull;
        } else {
            value = toBigDecimalArray(map(env.serverMeta()), env, nonNull, PARAM_ERROR_HANDLER);
        }
        return value;
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, final Object nonNull) throws CriteriaException {
        if (nonNull instanceof String) {
            return (String) nonNull;
        }

        final Class<?> sourceType = nonNull.getClass(), componentType;
        if (!sourceType.isArray()
                || (this.javaType != Object.class
                && ArrayUtils.dimensionOf(sourceType) != ArrayUtils.dimensionOf(this.javaType))) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }

        final BiConsumer<Object, Consumer<String>> consumer;
        if ((componentType = ArrayUtils.underlyingComponent(sourceType)) == BigDecimal.class) {
            consumer = BigDecimalArrayType::appendToText;
        } else if (componentType == String.class) {
            consumer = StringArrayType::appendToText;
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }

        final StringBuilder builder = new StringBuilder();
        try {
            PostgreArrays.toArrayText(nonNull, consumer, builder);
        } catch (Exception e) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, e);
        }
        return builder.toString();
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, Object nonNull) throws DataAccessException {
        return toBigDecimalArray(dataType, env, nonNull, ACCESS_ERROR_HANDLER);
    }


    private Object toBigDecimalArray(DataType dataType, MappingEnv env, final Object nonNull, ErrorHandler errorHandler) {
        final Object value;
        if (dataType == PostgreType.MONEY_ARRAY) {
            // TODO handle postgre money array
            throw new UnsupportedOperationException();
        } else if (nonNull instanceof String) {
            try {
                value = PostgreArrays.parseArray((String) nonNull, false, BigDecimalArrayType::parseDecimal, _Constant.COMMA,
                        dataType, this, errorHandler);
            } catch (Exception e) {
                throw errorHandler.apply(this, dataType, nonNull, e);
            }
        } else if (this.javaType.isInstance(nonNull)) {
            value = nonNull;
        } else {
            throw errorHandler.apply(this, dataType, nonNull, null);
        }
        return value;
    }


    /*-------------------below static methods -------------------*/

    public static BigDecimal parseDecimal(String text, int offset, int end) {
        return new BigDecimal(text.substring(offset, end));
    }

    public static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof BigDecimal)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(((BigDecimal) element).toPlainString());
    }


}
