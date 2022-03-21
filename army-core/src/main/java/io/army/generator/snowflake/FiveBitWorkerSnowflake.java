package io.army.generator.snowflake;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public final class FiveBitWorkerSnowflake implements _Snowflake {


    private static final ConcurrentMap<String, FiveBitWorkerSnowflake> INSTANCE_MAP = new ConcurrentHashMap<>();

    public synchronized static FiveBitWorkerSnowflake build(final long startTime, final Worker worker) {
        return null;
    }

    public FiveBitWorkerSnowflake() {

    }

    @Override
    public final long getWorkerBits() {
        return 5L;
    }

    @Override
    public long next() {
        return 0;
    }

    @Override
    public String nextAsString() {
        return null;
    }

    @Override
    public String nextAsString(long suffixNumber) {
        return null;
    }

    @Override
    public BigInteger next(long suffixNumber) {
        return null;
    }

    @Override
    public long getWorkerId() {
        return 0;
    }

    @Override
    public long getDataCenterId() {
        return 0;
    }

    @Override
    public long getStartTime() {
        return 0;
    }
}
