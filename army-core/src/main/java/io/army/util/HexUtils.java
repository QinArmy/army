package io.army.util;

import java.nio.charset.StandardCharsets;

public abstract class HexUtils {

    protected HexUtils() {
        throw new UnsupportedOperationException();
    }


    private static final byte[] UPPER_CASE_HEX_DIGITS = new byte[]{
            (byte) '0', (byte) '1', (byte) '2', (byte) '3'
            , (byte) '4', (byte) '5', (byte) '6', (byte) '7'
            , (byte) '8', (byte) '9', (byte) 'A', (byte) 'B'
            , (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'};

    private static final byte[] LOWER_CASE_HEX_DIGITS = new byte[]{
            (byte) '0', (byte) '1', (byte) '2', (byte) '3'
            , (byte) '4', (byte) '5', (byte) '6', (byte) '7'
            , (byte) '8', (byte) '9', (byte) 'a', (byte) 'b'
            , (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'};


    public static String hexEscapesText(boolean upperCase, byte[] bufferArray) {
        return new String(hexEscapes(upperCase, bufferArray, 0, bufferArray.length), StandardCharsets.UTF_8);
    }

    public static String hexEscapesText(boolean upperCase, byte[] bufferArray, final int length) {
        return new String(hexEscapes(upperCase, bufferArray, 0, length), StandardCharsets.UTF_8);
    }

    public static String hexEscapesText(boolean upperCase, byte[] bufferArray, final int offset, final int end) {
        return new String(hexEscapes(upperCase, bufferArray, offset, end), StandardCharsets.UTF_8);
    }

    public static byte[] hexEscapes(boolean upperCase, byte[] bufferArray) {
        return hexEscapes(upperCase, bufferArray, 0, bufferArray.length);
    }

    public static byte[] hexEscapes(boolean upperCase, byte[] bufferArray, final int end) {
        return hexEscapes(upperCase, bufferArray, 0, end);
    }

    public static byte[] hexEscapes(boolean upperCase, final byte[] bufferArray, final int offset, final int end) {
        if (offset < 0 || end > bufferArray.length || offset > end) {
            throw offsetAndEndError(bufferArray, offset, end);
        }
        final byte[] hexDigits = upperCase ? UPPER_CASE_HEX_DIGITS : LOWER_CASE_HEX_DIGITS;
        final byte[] hexDigitArray = new byte[(end - offset) << 1];
        byte b;
        for (int i = offset, j = 0; i < end; i++, j += 2) {
            b = bufferArray[i];
            hexDigitArray[j] = hexDigits[(b >> 4) & 0xF]; // write highBits
            hexDigitArray[j + 1] = hexDigits[b & 0xF]; // write lowBits
        }
        return hexDigitArray;
    }

    public static byte[] decodeHex(byte[] hexBytes) {
        return decodeHex(hexBytes, 0, hexBytes.length);
    }

    public static byte[] decodeHex(byte[] hexBytes, final int end) {
        return decodeHex(hexBytes, 0, end);
    }

    public static byte[] decodeHex(final byte[] hexBytes, final int offset, final int end) {
        if (offset < 0 || end > hexBytes.length || offset > end) {
            throw offsetAndEndError(hexBytes, offset, end);
        }
        final byte[] digitArray = new byte[(end - offset) >> 1];
        final int num0 = '0', num9 = '9', a = 'a', f = 'f', A = 'A', F = 'F';
        final int intervalOfa = a - 10, intervalOfA = A - 10;
        for (int i = 0, j = offset; i < digitArray.length; i++, j += 2) {

            for (int k = j, b; k < j + 2; k++) {
                b = hexBytes[k];
                if (b >= num0 && b <= num9) {
                    b -= num0;
                } else if (b >= a && b <= f) {
                    b -= intervalOfa;
                } else if (b >= A && b <= F) {
                    b -= intervalOfA;
                } else {
                    throw new IllegalArgumentException("non-hex");
                }
                digitArray[i] = (byte) ((digitArray[i] << 4) | b);
            }

        }
        return digitArray;
    }

    public static String decodeHexAsString(final byte[] hexBytes, final int offset, final int end) {
        return new String(decodeHex(hexBytes, offset, end), StandardCharsets.UTF_8);
    }

    public static String decodeHexAsString(final String hexStr) {
        final byte[] hexBytes = hexStr.getBytes(StandardCharsets.UTF_8);
        return new String(decodeHex(hexBytes, 0, hexBytes.length), StandardCharsets.UTF_8);
    }


    public static IllegalArgumentException requiredText(String paramName) {
        return new IllegalArgumentException(String.format("%s must have text", paramName));
    }

    private static IllegalArgumentException offsetAndEndError(byte[] array, final int offset, final int end) {
        return new IllegalArgumentException(
                String.format("offset[%s],end[%s],length[%s] not match", offset, end, array.length));
    }


}
