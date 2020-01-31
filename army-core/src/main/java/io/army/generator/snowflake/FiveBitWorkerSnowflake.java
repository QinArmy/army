package io.army.generator.snowflake;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public final class FiveBitWorkerSnowflake extends AbstractSnowflake {


    private static final ConcurrentMap<String, FiveBitWorkerSnowflake> INSTANCE_MAP = new ConcurrentHashMap<>();

    public synchronized static FiveBitWorkerSnowflake getInstance(final long startTime, final Worker worker) {
        return INSTANCE_MAP.computeIfAbsent(
                String.valueOf(startTime) + worker.getDataCenterId() + worker.getWorkerId()
                , k -> new FiveBitWorkerSnowflake(startTime, worker.getDataCenterId(), worker.getWorkerId()));
    }

    public FiveBitWorkerSnowflake(long startTime, long workerId, long dataCenterId) {
        super(startTime, workerId, dataCenterId);
    }

    @Override
    public final long getWorkerBits() {
        return 5L;
    }

}
