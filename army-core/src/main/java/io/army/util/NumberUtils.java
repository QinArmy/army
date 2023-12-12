package io.army.util;


public abstract class NumberUtils {

    protected NumberUtils() {
        throw new UnsupportedOperationException();
    }


    public static byte[] toBinaryBytes(final int value, final boolean bigEndian) {
        final byte[] bytes = new byte[4];
        if (bigEndian) {
            for (int i = 0, bits = 24; i < bytes.length; i++, bits -= 8) {
                bytes[i] = (byte) (value >> bits);
            }
        } else {
            for (int i = 0, bits = 0; i < bytes.length; i++, bits += 8) {
                bytes[i] = (byte) (value >> bits);
            }
        }
        return bytes;
    }

    public static byte[] toBinaryBytes(final long value, final boolean bigEndian) {
        final byte[] bytes = new byte[8];
        if (bigEndian) {
            for (int i = 0, bits = 56; i < bytes.length; i++, bits -= 8) {
                bytes[i] = (byte) (value >> bits);
            }
        } else {
            for (int i = 0, bits = 0; i < bytes.length; i++, bits += 8) {
                bytes[i] = (byte) (value >> bits);
            }
        }
        return bytes;
    }


    public static int readInt(final boolean bigEndian, final byte[] wkbArray, final int offset) {
        final int end = offset + 4;

        if (offset < 0) {
            throw new IllegalArgumentException("offset error");
        } else if (wkbArray.length < end) {
            throw new IllegalArgumentException("overflow");
        }
        int value = 0;
        if (bigEndian) {
            for (int i = offset, bitCount = 24; i < end; i++, bitCount -= 8) {
                value |= ((wkbArray[i] & 0xff) << bitCount);
            }
        } else {
            for (int i = offset, bitCount = 0; i < end; i++, bitCount += 8) {
                value |= ((wkbArray[i] & 0xff) << bitCount);
            }
        }
        return value;
    }

    public static long readLong(final boolean bigEndian, final byte[] wkbArray, final int offset) {
        final int end = offset + 8;

        if (offset < 0) {
            throw new IllegalArgumentException("offset error");
        } else if (wkbArray.length < end) {
            throw new IllegalArgumentException("overflow");
        }
        long value = 0;
        if (bigEndian) {
            for (int i = offset, bitCount = 56; i < end; i++, bitCount -= 8) {
                value |= ((wkbArray[i] & 0xffL) << bitCount);
            }
        } else {
            for (int i = offset, bitCount = 0; i < end; i++, bitCount += 8) {
                value |= ((wkbArray[i] & 0xffL) << bitCount);
            }
        }
        return value;
    }

    /**
     * Determine whether the given {@code value} String indicates a hex number,
     * i.e. needs to be passed into {@code Integer.decode} instead of
     * {@code Integer.valueOf}, etc.
     */
    public static boolean isHexNumber(final String value) {
        final int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }


}
