package io.army.dialect;

public abstract class _Literals {

    protected _Literals() {
        throw new UnsupportedOperationException();
    }


    private static final char[] LOWER_CASE_HEX_DIGITS = new char[]{
            '0', '1', '2', '3'
            , '4', '5', '6', '7'
            , '8', '9', 'a', 'b'
            , 'c', 'd', 'e', 'f'};


    public static char[] hexEscapes(final byte[] dataBytes) {
        final int bytesLength = dataBytes.length;
        final char[] hexDigitArray = new char[bytesLength << 1];
        byte b;
        for (int i = 0, j = 0; i < bytesLength; i++, j += 2) {
            b = dataBytes[i];
            hexDigitArray[j] = LOWER_CASE_HEX_DIGITS[(b >> 4) & 0xF]; // write highBits
            hexDigitArray[j + 1] = LOWER_CASE_HEX_DIGITS[b & 0xF]; // write lowBits
        }
        return hexDigitArray;
    }


}
