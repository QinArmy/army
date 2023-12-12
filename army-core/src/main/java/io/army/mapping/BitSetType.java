package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.BitSetArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
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
 ** @since 1.0
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
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BitSetArrayType.LINEAR;
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
    public BitSet convert(final MappingEnv env, final Object source) throws CriteriaException {
        return toBitSet(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(final DataType dataType, final MappingEnv env, final Object source) {
        final Object value;
        switch ((((SqlType) dataType).database())) {
            case MySQL:
                value = bitwiseToLong(this, dataType, source, PARAM_ERROR_HANDLER);
                break;
            case PostgreSQL:
                value = bitwiseToString(this, dataType, source, PARAM_ERROR_HANDLER);
                break;
            default:
                throw outRangeOfSqlType(dataType, source);
        }
        return value;
    }

    @Override
    public BitSet afterGet(final DataType dataType, final MappingEnv env, final Object source) {
        return toBitSet(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    public static long bitwiseToLong(final MappingType type, final DataType dataType, final Object source,
                                     final ErrorHandler errorHandler) {
        final long value;
        if (source instanceof Long) {
            value = (Long) source;
        } else if (source instanceof Integer) {
            value = ((Integer) source) & 0xffff_ffffL;
        } else if (source instanceof BitSet) {
            final BitSet v = (BitSet) source;
            if (v.length() > 64) {
                throw errorHandler.apply(type, dataType, source, null);
            } else if (v.length() == 0) {
                value = 0L;
            } else {
                value = v.toLongArray()[0];
            }
        } else if (source instanceof Short) {
            value = ((Short) source) & 0xffffL;
        } else if (source instanceof Byte) {
            value = ((Byte) source) & 0xffL;
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? 1L : 0L;
        } else if (source instanceof BigInteger) {
            final BigInteger v = (BigInteger) source;
            try {
                value = v.longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) source, 2);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof long[]) {
            final long[] v = (long[]) source;
            switch (v.length) {
                case 0:
                    value = 0L;
                    break;
                case 1:
                    value = v[0];
                    break;
                default:
                    throw errorHandler.apply(type, dataType, source, null);
            }
        } else if (source instanceof byte[]) {
            final byte[] v = (byte[]) source;
            if (v.length == 0) {
                value = 0L;
            } else if (v.length < 9) {
                long bits = 0L;
                for (int i = 0, bitNum = 0; i < v.length; i++, bitNum += 8) {
                    bits |= ((v[i] & 0xffL) << bitNum);
                }
                value = bits;
            } else {
                throw errorHandler.apply(type, dataType, source, null);
            }
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }

    public static String bitwiseToString(final MappingType type, final DataType dataType, final Object source,
                                         final ErrorHandler errorHandler) {
        final String value;
        if (source instanceof String) {
            value = (String) source;
            if (!_StringUtils.isBinary(value)) {
                throw errorHandler.apply(type, dataType, source, null);
            }
        } else if (source instanceof Long) {
            value = Long.toBinaryString((Long) source);
        } else if (source instanceof Integer) {
            value = Integer.toBinaryString((Integer) source);
        } else if (source instanceof BitSet) {
            value = littleEndianToBitString(((BitSet) source).toLongArray());
        } else if (source instanceof Short) {
            value = Integer.toBinaryString(((Short) source) & 0xffff);
        } else if (source instanceof Byte) {
            value = Integer.toBinaryString(((Byte) source) & 0xff);
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? "1" : "0";
        } else if (source instanceof BigInteger) {
            value = ((BigInteger) source).toString(2);
        } else if (source instanceof long[]) {
            value = littleEndianToBitString((long[]) source);
        } else if (source instanceof byte[]) {
            value = littleEndianToBitString(BitSet.valueOf((byte[]) source).toLongArray());
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }


    public static BitSet toBitSet(final MappingType type, final DataType dataType, final Object source,
                                  final ErrorHandler errorHandler) {
        final BitSet value;
        if (source instanceof BitSet) {
            value = (BitSet) source;
        } else if (source instanceof Long) {
            value = BitSet.valueOf(new long[]{(Long) source});
        } else if (source instanceof Integer) {
            final int v = (Integer) source;
            value = BitSet.valueOf(new long[]{v & 0xffff_ffffL});
        } else if (source instanceof Short) {
            final short v = (Short) source;
            value = BitSet.valueOf(new long[]{v & 0xffffL});
        } else if (source instanceof Byte) {
            value = BitSet.valueOf(new byte[]{(Byte) source});
        } else if (source instanceof Boolean) {
            value = new BitSet(1);
            value.set(0, (Boolean) source);
        } else if (source instanceof BigInteger) {
            try {
                final String v = ((BigInteger) source).toString(2);
                value = _StringUtils.bitStringToBitSet(v, 0, v.length());
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof long[]) {
            value = BitSet.valueOf((long[]) source);
        } else if (source instanceof byte[]) {
            value = BitSet.valueOf((byte[]) source);
        } else if (source instanceof String) {
            try {
                String v = (String) source;
                value = _StringUtils.bitStringToBitSet(v, 0, v.length());
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, source, null);
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
