package io.army.generator.snowflake;

import java.math.BigInteger;

/**
 * @see AbstractSnowflake
 * @see FiveBitWorkerSnowflake
 */
public interface Snowflake {

    /** bit number of worker(dataCenterId + workerId) */
    long WORKER_BIT_SIZE = 10L;

    /** bit number of sequence id */
    long SEQUENCE_BITS = 12L;

    /** bit number that timestamp left shift */
    long TIMESTAMP_LEFT_SHIFT = WORKER_BIT_SIZE + SEQUENCE_BITS;

    /** max value of sequence */
    long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);


    long next();

    String nextAsString();

    String nextAsString(long suffixNumber);

    BigInteger next(long suffixNumber);

    long getWorkerBits();

    long getWorkerId();

    long getDataCenterId();

    long getStartTime();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
