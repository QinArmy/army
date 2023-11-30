package io.army.util;

import java.util.BitSet;

public abstract class _MappingUtils {

    private _MappingUtils() {
        throw new UnsupportedOperationException();
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





}
