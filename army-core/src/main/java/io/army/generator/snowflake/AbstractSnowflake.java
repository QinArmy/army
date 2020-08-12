package io.army.generator.snowflake;

import io.army.util.Assert;
import io.army.util.TimeUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractSnowflake implements Snowflake {


    private final long startTime;

    /**
     *
     */
    private final long workerId;

    /**
     *
     */
    private final long dataCenterId;

    /** (0~4095) */
    private long sequence = 0L;

    private long lastTimestamp = -1L;

    protected AbstractSnowflake(long startTime, long workerId, long dataCenterId) {

        if (startTime < 0) {
            throw new IllegalArgumentException("startTime must great than or equals 0");
        }
        final long workerBits = getWorkerBits();
        final long maxWorkerId = ~(-1L << workerBits);
        final long maxDataCenterId = ~(-1L << (WORKER_BIT_SIZE - workerBits));

        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can'field be greater than %d or less than 0", maxWorkerId));
        }

        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("dataCenter Id can'field be greater than %d or less than 0", maxDataCenterId));
        }

        this.startTime = startTime;
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    @Override
    public final synchronized long next() {
        long timestamp = SystemClock.now();

        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5L) {
                try {
                    wait(offset << 1L);
                    timestamp = SystemClock.now();
                    if (timestamp < lastTimestamp) {
                        throw new IllegalStateException(
                                String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalStateException(
                        String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1L) & SEQUENCE_MASK;
            if (sequence == 0) {
                // block util then millis
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // reset sequence
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        final long dataCenterIdShift = SEQUENCE_BITS + getWorkerBits();

        return ((timestamp - startTime) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << SEQUENCE_BITS)
                | sequence;
    }

    @Override
    public final BigInteger next(long suffixNumber) {
        return new BigInteger(nextAsString(suffixNumber));
    }

    @Override
    public final String nextAsString(long suffixNumber) {
        Assert.isTrue(suffixNumber >= 0L, "suffixNumber must great than 0");
        return LocalDateTime.now().format(TimeUtils.dateTimeFormatter(TimeUtils.CLOSE_DATE_FORMAT))
                + this.next()
                + suffixWithZero(suffixNumber);
    }

    @Override
    public final String nextAsString() {
        return Long.toString(next());
    }

    @Override
    public final long getWorkerId() {
        return workerId;
    }

    @Override
    public final long getDataCenterId() {
        return dataCenterId;
    }

    @Override
    public final long getStartTime() {
        return startTime;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(startTime, dataCenterId, workerId);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!getClass().isInstance(obj)) {
            return false;
        }
        AbstractSnowflake snowflake = (AbstractSnowflake) obj;
        return startTime == snowflake.startTime
                && dataCenterId == snowflake.dataCenterId
                && workerId == snowflake.workerId
                ;
    }

    @Override
    public final String toString() {
        return String.format("startTime:%s,dataCenterId:%s,workerId:%s"
                , startTime
                , dataCenterId
                , workerId);
    }


    /*################################## blow protected method ##################################*/

    /**
     * block util then millis
     *
     * @return current millis
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = SystemClock.now();
        while (timestamp <= lastTimestamp) {
            timestamp = SystemClock.now();
        }
        return timestamp;
    }

    private String suffixWithZero(long number) {
        String str = Long.toString(number % 10_0000);
        if (str.length() < 5) {
            char[] chars = new char[5 - str.length()];
            Arrays.fill(chars, '0');
            str = new String(chars) + str;
        }
        return str;
    }

}
