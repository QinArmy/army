/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.generator.snowflake;

import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Snowflake {

    private static final ConcurrentMap<Snowflake, SnowflakeReference> INSTANCE_MAP = new ConcurrentHashMap<>();


    // private static final Logger LOG = LoggerFactory.getLogger(Snowflake.class);

    public static synchronized Snowflake create(final long startTime, final long dataCenterId, final long workerId) {
        if (startTime < 0) {
            throw new IllegalArgumentException("startTime must great than or equals 0");
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            String m = String.format("Data center id[%s] couldn't be greater than %s or less than 0"
                    , dataCenterId, MAX_DATA_CENTER_ID);
            throw new IllegalArgumentException(m);
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            String m = String.format("Worker id[%s] couldn't be greater than %s or less than 0"
                    , workerId, MAX_WORKER_ID);
            throw new IllegalArgumentException(m);
        }

        final Snowflake newInstance, currentInstance;
        newInstance = new Snowflake(startTime, dataCenterId, workerId);

        final SnowflakeReference reference;
        reference = INSTANCE_MAP.computeIfAbsent(newInstance, SnowflakeReference::new);
        currentInstance = reference.get();
        if (currentInstance == null) {
            INSTANCE_MAP.put(newInstance, new SnowflakeReference(newInstance));
        }
        return currentInstance == null ? newInstance : currentInstance;
    }


    /** bit number of worker(dataCenterId + workerId) */
    public static final byte WORKER_BIT_SIZE = 10;

    /** bit number of sequence id */
    public static final byte SEQUENCE_BITS = 12;

    /** bit number that timestamp left shift */
    public static final byte TIMESTAMP_LEFT_SHIFT = WORKER_BIT_SIZE + SEQUENCE_BITS;

    /** max value of sequence */
    public static final short SEQUENCE_MASK = ~(-1 << SEQUENCE_BITS);

    public static final byte DATA_CENTER_SHIFT = SEQUENCE_BITS + 5;

    public static final byte MAX_WORKER_ID = ~(-1 << 5);

    public static final byte MAX_DATA_CENTER_ID = MAX_WORKER_ID;

    public final long startTime;

    public final long workerId;

    public final long dataCenterId;

    /** (0~4095) */
    private long sequence = 0L;

    private long lastTimestamp;


    private Snowflake(final long startTime, final long dataCenterId, final long workerId) {
        this.startTime = startTime;
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;

        this.lastTimestamp = SystemClock.now();
    }

    public long next() {
        synchronized (this) {
            long timestamp;
            timestamp = System.currentTimeMillis();
            final long lastTimestamp = this.lastTimestamp;

            if (timestamp < lastTimestamp) {
                timestamp = waitClock(lastTimestamp, timestamp);
            }
            long sequence = this.sequence;

            if (timestamp != lastTimestamp) {
                // reset sequence
                sequence = 0L;
            } else if ((sequence = (++sequence) & SEQUENCE_MASK) == 0) {
                // block util then millis
                timestamp = System.currentTimeMillis();
                if (timestamp <= lastTimestamp) {
                    try {
                        this.wait(1L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    timestamp = System.currentTimeMillis();

                }
                sequence = (++sequence) & SEQUENCE_MASK;
            }
            this.lastTimestamp = timestamp;
            this.sequence = sequence;
            return ((timestamp - this.startTime) << TIMESTAMP_LEFT_SHIFT)
                    | (this.dataCenterId << DATA_CENTER_SHIFT)
                    | (this.workerId << SEQUENCE_BITS)
                    | sequence;
        }


    }


    @Override
    public int hashCode() {
        return Objects.hash(this.startTime, this.dataCenterId, this.workerId);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof Snowflake) {
            final Snowflake s = (Snowflake) obj;
            match = s.startTime == this.startTime
                    && s.dataCenterId == this.dataCenterId
                    && s.workerId == this.workerId;
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public String toString() {
        return String.format("[%s startTime:%s,dataCenterId:%s,workerId:%s]"
                , Snowflake.class.getName()
                , this.startTime
                , this.dataCenterId
                , this.workerId);
    }


    /*################################## blow protected method ##################################*/

    private long waitClock(final long lastTimestamp, long timestamp) {
        final long offset = lastTimestamp - timestamp;
        if (offset <= 5L) {
            try {
                this.wait(offset << 1L);
                timestamp = System.currentTimeMillis();
                if (timestamp < lastTimestamp) {
                    throw clockMovedBackwards(offset);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw clockMovedBackwards(offset);
        }
        return timestamp;
    }


    private static IllegalStateException clockMovedBackwards(long offset) {
        String m = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", offset);
        return new IllegalStateException(m);
    }


    private static final class SnowflakeReference extends SoftReference<Snowflake> {

        private SnowflakeReference(Snowflake referent) {
            super(referent);
        }

        @Override
        public void clear() {
            final Snowflake referent = this.get();
            if (referent != null) {
                INSTANCE_MAP.remove(referent, this);
            }
            super.clear();
        }

    }//SnowflakeReference


}
