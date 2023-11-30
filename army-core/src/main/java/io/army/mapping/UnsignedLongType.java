package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * <p>
 * This class representing the mapping from {@link BigInteger} to (unsigned) bigint.
 * </p>
 *
 * @see BigInteger
 */
public final class UnsignedLongType extends _NumericType._UnsignedIntegerType {

    public static UnsignedLongType from(final Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(UnsignedLongType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BigInteger MAX_VALUE = new BigInteger(Long.toUnsignedString(-1L));

    public static final UnsignedLongType INSTANCE = new UnsignedLongType();

    /**
     * private constructor
     */
    private UnsignedLongType() {
    }

    @Override
    public Class<?> javaType() {
        return BigInteger.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.BIG_LONG;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.BIGINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreType.DECIMAL;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public BigInteger convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return toUnsignedBigInteger(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Number beforeBind(final SqlType type, MappingEnv env, final Object nonNull) {
        final BigInteger integerValue;
        integerValue = toUnsignedBigInteger(this, nonNull, PARAM_ERROR_HANDLER_0);
        final Number value;
        switch (type.database()) {
            case MySQL:
                value = integerValue;
                break;
            case PostgreSQL:
                value = new BigDecimal(integerValue);
                break;
            default:
                throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        return toUnsignedBigInteger(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static BigInteger toUnsignedBigInteger(final MappingType type, final Object nonNull,
                                                   final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final BigInteger value;
        value = BigIntegerType.toBigInteger(type, nonNull, errorHandler);
        if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
