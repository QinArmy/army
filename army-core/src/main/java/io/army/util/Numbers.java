package io.army.util;


import java.math.BigInteger;

public abstract class Numbers {

    protected Numbers() {
        throw new UnsupportedOperationException();
    }

    public static final BigInteger MAX_UNSIGNED_LONG = new BigInteger(Long.toUnsignedString(-1L));


}
