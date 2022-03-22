package io.army.generator.snowflake;

import java.util.Objects;

public final class Worker {

    public static final Worker ZERO = new Worker(0, 0);


    public static Worker create(final long dataCenterId, final long workerId) {
        Worker worker;
        if (dataCenterId == 0L && workerId == 0L) {
            worker = ZERO;
        } else if (dataCenterId < 0L || dataCenterId > Snowflake.MAX_DATA_CENTER_ID) {
            String m = String.format("dataCenterId must in [0,%s]", Snowflake.MAX_DATA_CENTER_ID);
            throw new IllegalArgumentException(m);
        } else if (workerId < 0L || workerId > Snowflake.MAX_WORKER_ID) {
            String m = String.format("workerId must in [0,%s]", Snowflake.MAX_WORKER_ID);
            throw new IllegalArgumentException(m);
        } else {
            worker = new Worker(dataCenterId, workerId);
        }
        return worker;
    }

    public final long dataCenterId;

    public final long workerId;

    private Worker(final long dataCenterId, final long workerId) {
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dataCenterId, this.workerId);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof Worker) {
            final Worker w = (Worker) obj;
            match = w.dataCenterId == this.dataCenterId
                    && w.workerId == this.workerId;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format("[%ss hash:%s,dataCenterId:%s,workerId:%s]"
                , System.identityHashCode(this), Worker.class.getName(), dataCenterId, workerId);
    }


}
