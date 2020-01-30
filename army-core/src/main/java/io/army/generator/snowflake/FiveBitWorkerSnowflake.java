package io.army.generator.snowflake;

import io.army.util.Assert;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.army.util.TimeUtils.CLOSE_DATE_FORMATTER;

/**
 *
 */
public class FiveBitWorkerSnowflake implements Snowflake {


    /** bit number of worker id */
    private static final long WORKER_ID_BITS = 5L;
    /** bit number of data center id */
    private static final long DATA_CENTER_ID_BITS = 5L;
    /** bit number of sequence id */
    private static final long SEQUENCE_BITS = 12L;

    /** bit number that worker id left shift */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /** bit number that data center id left shift */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /** max value of worker id */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /** max value of data center id */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /** bit number that timestamp left shift */
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /** max value of sequence */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final ConcurrentMap<String, FiveBitWorkerSnowflake> INSTANCE_MAP = new ConcurrentHashMap<>();

    public synchronized static FiveBitWorkerSnowflake getInstance(final long startTime, final Worker worker) {
        return INSTANCE_MAP.computeIfAbsent(
                String.valueOf(startTime) + worker.getDataCenterId() + worker.getWorkerId()
                , k -> new FiveBitWorkerSnowflake(startTime, worker.getDataCenterId(), worker.getWorkerId()));
    }


    /*##################### properties ##########################*/

    private final long startTime;

    /** (0~31) */
    private final long workerId;

    /** (0~31) */
    private final long dataCenterId;

    /** (0~4095) */
    private long sequence = 0L;

    private long lastTimestamp = -1L;

    private FiveBitWorkerSnowflake(long startTime, long workerId, long dataCenterId) {

        if (startTime < 0) {
            throw new IllegalArgumentException("startTime must great than or equals 0");
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }

        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("dataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }

        this.startTime = startTime;
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    @Override
    public synchronized long next() {
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
                // block util next millis
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // reset sequence
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - startTime) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    @Override
    public BigInteger next(long suffixNumber){
        return new BigInteger(nextAsString(suffixNumber));
    }

    @Override
    public String nextAsString(long suffixNumber){
        Assert.isTrue(suffixNumber >= 0L, "suffixNumber must great than 0");
        return LocalDate.now().format(CLOSE_DATE_FORMATTER) + this.next() + suffixWithZero(suffixNumber);
    }

    @Override
    public String nextAsString() {
        return String.valueOf(next());
    }

    @Override
    public long getWorkerId() {
        return workerId;
    }

    @Override
    public long getDataCenterId() {
        return dataCenterId;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getNumberOfWorkerBit() {
        return FiveBitWorkerSnowflake.WORKER_ID_BITS;
    }

    /*################################## blow private method ##################################*/

    /**
     * block util next millis
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
        String str = String.valueOf(number % 10_0000);
        char[] chars = new char[5 - str.length()];
        Arrays.fill(chars, '0');
        return new String(chars) + str;
    }


}
