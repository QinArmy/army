package io.army.util;

import java.util.Objects;
import java.util.StringJoiner;

public final class Precision {

    public static final Precision EMPTY = new Precision(0, 0);

    public static final Precision DEFAULT_CHAR_PRECISION = new Precision(255, 0);

    public static final Precision DEFAULT_DECIMAL_PRECISION = new Precision(14, 2);

    public static final Precision DEFAULT_INT_PRECISION = new Precision(11, 0);

    public static final Precision DEFAULT_BIGINT_PRECISION = new Precision(20, 0);


    private final int precision;

    private final int scale;

    public Precision(int precision, int scale) {
        this.precision = precision;
        this.scale = scale;
    }

    public int getPrecision() {
        return precision;
    }

    public Integer getScale() {
        return scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Precision)) {
            return false;
        }
        Precision precision1 = (Precision) o;
        return precision == precision1.precision &&
                Objects.equals(scale, precision1.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(precision, scale);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Precision.class.getSimpleName() + "[", "]")
                .add("precision=" + precision)
                .add("scale=" + scale)
                .toString();
    }
}
