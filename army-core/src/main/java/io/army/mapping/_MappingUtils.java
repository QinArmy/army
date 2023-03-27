package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.sqltype.SqlType;
import io.army.util._StringUtils;

import java.math.BigInteger;
import java.util.BitSet;

public abstract class _MappingUtils {

    private _MappingUtils() {
        throw new UnsupportedOperationException();
    }


    public static long bitwiseToLong(final SqlType type, final Object nonNull) throws CriteriaException {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer) {
            value = ((Integer) nonNull) & 0xffff_ffffL;
        } else if (nonNull instanceof BitSet) {
            final BitSet v = (BitSet) nonNull;
            if (v.length() > 64) {
                throw AbstractMappingType.valueOutRange(type, nonNull, null);
            } else if (v.length() == 0) {
                value = 0L;
            } else {
                value = v.toLongArray()[0];
            }
        } else if (nonNull instanceof Short) {
            value = ((Short) nonNull) & 0xffffL;
        } else if (nonNull instanceof Byte) {
            value = ((Byte) nonNull) & 0xffL;
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            try {
                value = v.longValueExact();
            } catch (ArithmeticException e) {
                throw AbstractMappingType.valueOutRange(type, nonNull, e);
            }
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseUnsignedLong((String) nonNull, 2);
            } catch (NumberFormatException e) {
                throw AbstractMappingType.valueOutRange(type, nonNull, e);
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
                    throw AbstractMappingType.valueOutRange(type, nonNull, null);
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
                throw AbstractMappingType.valueOutRange(type, nonNull, null);
            }
        } else {
            throw AbstractMappingType.outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    public static String bitwiseToString(final SqlType type, final Object nonNull) {
        final String value;
        if (nonNull instanceof String) {
            value = (String) nonNull;
            if (!_StringUtils.isBinary(value)) {
                throw AbstractMappingType.valueOutRange(type, nonNull, null);
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
        } else if (nonNull instanceof BigInteger) {
            value = ((BigInteger) nonNull).toString(2);
        } else if (nonNull instanceof long[]) {
            value = littleEndianToBitString((long[]) nonNull);
        } else if (nonNull instanceof byte[]) {
            value = littleEndianToBitString(BitSet.valueOf((byte[]) nonNull).toLongArray());
        } else {
            throw AbstractMappingType.outRangeOfSqlType(type, nonNull);
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
