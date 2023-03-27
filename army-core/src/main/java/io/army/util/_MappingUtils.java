package io.army.util;

import io.army.ArmyException;
import io.army.mapping.MappingType;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.function.BiFunction;

public abstract class _MappingUtils {

    private _MappingUtils() {
        throw new UnsupportedOperationException();
    }


    public static long bitwiseToLong(final MappingType type, final Object nonNull,
                                     final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer) {
            value = ((Integer) nonNull) & 0xffff_ffffL;
        } else if (nonNull instanceof BitSet) {
            final BitSet v = (BitSet) nonNull;
            if (v.length() > 64) {
                throw errorHandler.apply(type, nonNull);
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
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) nonNull, 2);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, nonNull);
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
                    throw errorHandler.apply(type, nonNull);
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
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }

    public static String bitwiseToString(final MappingType type, final Object nonNull,
                                         final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final String value;
        if (nonNull instanceof String) {
            value = (String) nonNull;
            if (!_StringUtils.isBinary(value)) {
                throw errorHandler.apply(type, nonNull);
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
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


    public static BitSet bitStringToBitSet(final String bitStr) throws IllegalArgumentException {
        final int bitLength;
        bitLength = bitStr.length();
        if (bitLength == 0) {
            throw new IllegalArgumentException("bit string must non-empty.");
        }
        final BitSet bitSet = new BitSet(bitLength);
        final int maxIndex = bitLength - 1;
        for (int i = 0; i < bitLength; i++) {
            switch (bitStr.charAt(maxIndex - i)) {
                case '0':
                    bitSet.set(i, false);
                    break;
                case '1':
                    bitSet.set(i, true);
                    break;
                default:
                    throw new IllegalArgumentException("non-bit string");
            }
        }
        return bitSet;
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
