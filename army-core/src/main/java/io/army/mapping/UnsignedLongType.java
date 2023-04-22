package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PgSqlType;
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

    public static final UnsignedLongType INSTANCE = new UnsignedLongType();

    public static UnsignedLongType from(final Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(UnsignedLongType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BigInteger MAX_VALUE = new BigInteger(Long.toUnsignedString(-1L));


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
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLType.BIGINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PgSqlType.DECIMAL;
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
        return _convertToUnsignedBigInteger(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Number beforeBind(final SqlType type, MappingEnv env, final Object nonNull) {
        final BigInteger integerValue;
        integerValue = _convertToUnsignedBigInteger(this, nonNull, PARAM_ERROR_HANDLER_0);
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
        return _convertToUnsignedBigInteger(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static BigInteger _convertToUnsignedBigInteger(final MappingType type, final Object nonNull,
                                                           final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final BigInteger value;
        value = BigIntegerType._convertToBigInteger(type, nonNull, errorHandler);
        if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
