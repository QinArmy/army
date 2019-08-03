package io.army.util;

import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

public final class Precision {

    public static final Precision EMPTY = new Precision(0, null);

    public static final Precision DEFAULT_CHAR_PRECISION = new Precision(255, null);

    public static final Precision DEFAULT_INT_PRECISION = new Precision(11, null);

    public static final Precision DEFAULT_DECIMAL_PRECISION = new Precision(14, 2);

    private final int precision;

    @Nullable
    private final Integer scale;

    public Precision(int precision, Integer scale) {
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Precision precision1 = (Precision) o;
        return precision == precision1.precision &&
                scale.equals(precision1.scale);
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
