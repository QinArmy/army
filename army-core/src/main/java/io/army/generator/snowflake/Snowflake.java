package io.army.generator.snowflake;

import java.math.BigInteger;

public interface Snowflake {

    long next();

    String nextAsString();

    String nextAsString(long suffixNumber);

    BigInteger next(long suffixNumber);

    long getNumberOfWorkerBit();

    long getWorkerId();

    long getDataCenterId();

    long getStartTime();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
