package io.army.util;

import java.math.BigDecimal;

public abstract class Decimals {

    private Decimals() {
        throw new UnsupportedOperationException();
    }


    public static BigDecimal valueOf(String value) {
        return new BigDecimal(value);
    }

    public static BigDecimal valueOf(long value) {
        return BigDecimal.valueOf(value);
    }


    public static BigDecimal valueOf(double value) {
        return new BigDecimal(Double.toString(value));
    }


}
