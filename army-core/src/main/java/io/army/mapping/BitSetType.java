package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._MappingUtils;
import io.army.util._StringUtils;

import java.math.BigInteger;
import java.util.BitSet;

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


    public static BitSetType from(Class<?> fieldType) {
        if (fieldType != BitSet.class) {
            throw errorJavaType(BitSetType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BitSetType INSTANCE = new BitSetType();

    /**
     * private constructor
     */
    private BitSetType() {
    }

    @Override
    public Class<?> javaType() {
        return BitSet.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.BIT;
                break;
            case PostgreSQL:
                type = PostgreType.VARBIT;
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
        return convertToBitSet(this, map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(final DataType dataType, final MappingEnv env, final Object nonNull) {
        final Object value;
        switch ((((SqlType) dataType).database())) {
            case MySQL:
                value = bitwiseToLong(this, dataType, nonNull, PARAM_ERROR_HANDLER);
                break;
            case PostgreSQL:
                value = bitwiseToString(this, dataType, nonNull, PARAM_ERROR_HANDLER);
                break;
            default:
                throw outRangeOfSqlType(dataType, nonNull);
        }
        return value;
    }

    @Override
    public BitSet afterGet(final DataType dataType, final MappingEnv env, final Object nonNull) {
        return convertToBitSet(this, dataType, nonNull, ACCESS_ERROR_HANDLER);
    }


    public static long bitwiseToLong(final MappingType type, final DataType dataType, final Object nonNull,
                                     final ErrorHandler errorHandler) {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer) {
            value = ((Integer) nonNull) & 0xffff_ffffL;
        } else if (nonNull instanceof BitSet) {
            final BitSet v = (BitSet) nonNull;
            if (v.length() > 64) {
                throw errorHandler.apply(type, dataType, nonNull, null);
            } else if (v.length() == 0) {
                value = 0L;
            } else {
                value = v.toLongArray()[0];
            }
        } else if (nonNull instanceof Short) {
            value = ((Short) nonNull) & 0xffffL;
        } else if (nonNull instanceof Byte) {
            value = ((Byte) nonNull) & 0xffL;
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1L : 0L;
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            try {
                value = v.longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) nonNull, 2);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof long[]) {
            final long[] v = (long[]) nonNull;
            switch (v.length) {
                case 0:
                    value = 0L;
                    break;
                case 1:
                    value = v[0];
                    break;
                default:
                    throw errorHandler.apply(type, dataType, nonNull, null);
            }
        } else if (nonNull instanceof byte[]) {
            final byte[] v = (byte[]) nonNull;
            if (v.length == 0) {
                value = 0L;
            } else if (v.length < 9) {
                long bits = 0L;
                for (int i = 0, bitNum = 0; i < v.length; i++, bitNum += 8) {
                    bits |= ((v[i] & 0xffL) << bitNum);
                }
                value = bits;
            } else {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }

    public static String bitwiseToString(final MappingType type, final DataType dataType, final Object nonNull,
                                         final ErrorHandler errorHandler) {
        final String value;
        if (nonNull instanceof String) {
            value = (String) nonNull;
            if (!_StringUtils.isBinary(value)) {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
        } else if (nonNull instanceof Long) {
            value = Long.toBinaryString((Long) nonNull);
        } else if (nonNull instanceof Integer) {
            value = Integer.toBinaryString((Integer) nonNull);
        } else if (nonNull instanceof BitSet) {
            value = littleEndianToBitString(((BitSet) nonNull).toLongArray());
        } else if (nonNull instanceof Short) {
            value = Integer.toBinaryString(((Short) nonNull) & 0xffff);
        } else if (nonNull instanceof Byte) {
            value = Integer.toBinaryString(((Byte) nonNull) & 0xff);
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? "1" : "0";
        } else if (nonNull instanceof BigInteger) {
            value = ((BigInteger) nonNull).toString(2);
        } else if (nonNull instanceof long[]) {
            value = littleEndianToBitString((long[]) nonNull);
        } else if (nonNull instanceof byte[]) {
            value = littleEndianToBitString(BitSet.valueOf((byte[]) nonNull).toLongArray());
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


    private static BitSet convertToBitSet(final MappingType type, final DataType dataType, final Object nonNull,
                                          final ErrorHandler errorHandler) {
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
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


    private static String littleEndianToBitString(final long[] words) {
        if (words.length == 0) {
            return "0";
        }

        final StringBuilder builder = new StringBuilder(words.length);
        builder.append(Long.toBinaryString(words[words.length - 1]));
        long word;
        for (int i = words.length - 2; i > -1; i--) {
            word = words[i];
            for (int bitNum = 63; bitNum > -1; bitNum--) {
                if ((word & (1L << bitNum)) == 0) {
                    builder.append('0');
                } else {
                    builder.append('1');
                }
            }//inter for

        }//outer for

        return builder.toString();
    }


}
