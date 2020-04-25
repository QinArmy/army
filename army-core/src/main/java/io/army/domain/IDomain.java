package io.army.domain;

/**
 * created  on 2018/11/20.
 */
public interface IDomain {


    String NOW = "NOW()";

    String ZERO_YEAR = "$ZERO_YEAR$";

    String ZERO_DATE = "$ZERO_DATE$";

    String ZERO_DATE_TIME = "$ZERO_DATE_TIME$";

    String ZERO = "0";

    String ONE = "1";

    String DECIMAL_ZERO = "0.00";

    String N = "N";

    String Y = "Y";

    String UTF_8 = "UTF-8";

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

}
