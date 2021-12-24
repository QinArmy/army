package io.army.generator.snowflake;

import io.army.util._Assert;

public final class Worker {

    public static final Worker ZERO = new Worker(0, 0);

    private final long dataCenterId;

    private final long workerId;

    public Worker(long dataCenterId, long workerId) {
        _Assert.isTrue(dataCenterId >= 0 && dataCenterId <= 1024, "dataCenterId must belong [0,1024].");
        _Assert.isTrue(workerId >= 0 && workerId <= 1024, "workerId must belong [0,1024].");

        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(dataCenterId) + Long.hashCode(workerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Worker)) {
            return false;
        }
        Worker w = (Worker) obj;
        return this.dataCenterId == w.dataCenterId
                && this.workerId == w.workerId;
    }

    @Override
    public String toString() {
        return String.format("dataCenterId:%s,workerId:%s .",dataCenterId,workerId);
    }
}
