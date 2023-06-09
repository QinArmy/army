package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.util._MappingUtils;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link BitSet}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte},non-empty</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link Boolean}</li>
 *     <li>{@link String} , it must be bit string.</li>
 *     <li>{@code  byte[]}, non-empty</li>
 *     <li>{@code long[]}, non-empty</li>
 * </ul>
 *  to {@link BitSet}
 * </p>
 *
 * @since 1.0
 */
public final class BitSetType extends _ArmyNoInjectionMapping implements MappingType.SqlBitType {

    public static final BitSetType INSTANCE = new BitSetType();

    public static BitSetType from(Class<?> fieldType) {
        if (fieldType != BitSet.class) {
            throw errorJavaType(BitSetType.class, fieldType);
        }
        return INSTANCE;
    }

    private BitSetType() {
    }

    @Override
    public Class<?> javaType() {
        return BitSet.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.BIT;
                break;
            case Postgre:
                type = PostgreSqlType.VARBIT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }


    @Override
    public BitSet convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        return convertToBitSet(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Object beforeBind(final SqlType type, final MappingEnv env, final Object nonNull) {
        final Object value;
        switch (type.database()) {
            case MySQL:
                value = _MappingUtils.bitwiseToLong(this, nonNull, PARAM_ERROR_HANDLER_0);
                break;
            case Postgre:
                value = _MappingUtils.bitwiseToString(this, nonNull, PARAM_ERROR_HANDLER_0);
                break;
            default:
                throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public BitSet afterGet(final SqlType type, final MappingEnv env, final Object nonNull) {
        return convertToBitSet(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static BitSet convertToBitSet(final MappingType type, final Object nonNull,
                                          final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final BitSet value;
        if (nonNull instanceof BitSet) {
            value = (BitSet) nonNull;
        } else if (nonNull instanceof Long) {
            value = BitSet.valueOf(new long[]{(Long) nonNull});
        } else if (nonNull instanceof Integer) {
            final int v = (Integer) nonNull;
            value = BitSet.valueOf(new long[]{v & 0xffff_ffffL});
        } else if (nonNull instanceof Short) {
            final short v = (Short) nonNull;
            value = BitSet.valueOf(new long[]{v & 0xffffL});
        } else if (nonNull instanceof Byte) {
            value = BitSet.valueOf(new byte[]{(Byte) nonNull});
        } else if (nonNull instanceof Boolean) {
            value = new BitSet(1);
            value.set(0, (Boolean) nonNull);
        } else if (nonNull instanceof BigInteger) {
            value = _MappingUtils.bitStringToBitSet(((BigInteger) nonNull).toString(2));
        } else if (nonNull instanceof long[]) {
            value = BitSet.valueOf((long[]) nonNull);
        } else if (nonNull instanceof byte[]) {
            value = BitSet.valueOf((byte[]) nonNull);
        } else if (nonNull instanceof String) {
            try {
                value = _MappingUtils.bitStringToBitSet((String) nonNull);
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
