package io.army.generator.snowflake;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.time.temporal.ChronoField.*;

public final class Snowflake implements _Snowflake {

    private static final ConcurrentMap<Snowflake, Snowflake> INSTANCE_MAP = new ConcurrentHashMap<>();

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.NEVER)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter(Locale.ENGLISH);

    // private static final Logger LOG = LoggerFactory.getLogger(Snowflake.class);

    public static Snowflake create(final long startTime, final long workerId, final long dataCenterId
            , final long workerBits) {
        if (startTime < 0) {
            throw new IllegalArgumentException("startTime must great than or equals 0");
        }
        if (workerBits < 1 || workerBits > 9) {
            throw new IllegalArgumentException("workerBits must in [1,9]");
        }
        final long maxWorkerId, maxDataCenterId;
        maxWorkerId = ~(-1L << workerBits);
        maxDataCenterId = ~(-1L << (WORKER_BIT_SIZE - workerBits));

        if (workerId > maxWorkerId || workerId < 0) {
            String m = String.format("Worker id[%s] couldn't be greater than %s or less than 0", workerId, maxWorkerId);
            throw new IllegalArgumentException(m);
        }

        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            String m = String.format("Data center id[%s] couldn't be greater than %s or less than 0"
                    , dataCenterId, maxDataCenterId);
            throw new IllegalArgumentException(m);
        }
        final Snowflake newInstance, oldInstance;
        newInstance = new Snowflake(startTime, workerBits, dataCenterId, workerId);
        oldInstance = INSTANCE_MAP.putIfAbsent(newInstance, newInstance);
        return oldInstance == null ? newInstance : oldInstance;
    }


    private final long startTime;

    /**
     *
     */
    private final long workerId;

    /**
     *
     */
    private final long dataCenterId;

    private final long workerBits;

    private final long dataCenterIdShift;

    /** (0~4095) */
    private long sequence = 0L;

    private long lastTimestamp;


    private Snowflake(final long startTime, final long workerBits, final long dataCenterId, final long workerId) {
        this.startTime = startTime;
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.workerBits = workerBits;

        this.dataCenterIdShift = SEQUENCE_BITS + workerBits;
        this.lastTimestamp = SystemClock.now();
    }

    @Override
    public long next() {
        synchronized (this) {
            long timestamp;
            timestamp = SystemClock.now();
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
                timestamp = SystemClock.now();
                while (timestamp <= lastTimestamp) {
                    timestamp = SystemClock.now();
                }
                sequence = (++sequence) & SEQUENCE_MASK;
            }
            this.lastTimestamp = timestamp;
            this.sequence = sequence;
            return ((timestamp - this.startTime) << TIMESTAMP_LEFT_SHIFT)
                    | (this.dataCenterId << this.dataCenterIdShift)
                    | (this.workerId << SEQUENCE_BITS)
                    | sequence;
        }


    }


    @Override
    public BigInteger next(long suffixNumber) {
        return new BigInteger(nextAsString(suffixNumber));
    }


    @Override
    public String nextAsString(final long suffixNumber) {
        if (suffixNumber < 0) {
            throw new IllegalArgumentException("suffixNumber must non-negative");
        }
        final String suffix;
        suffix = suffixWithZero(suffixNumber);
        final StringBuilder builder = new StringBuilder(27 + suffix.length());
        builder.append(LocalDateTime.now().format(FORMATTER));

        final long nextSequence;
        nextSequence = this.next();
        return builder
                .append(nextSequence)
                .append(suffix)
                .toString();
    }

    @Override
    public String nextAsString() {
        return Long.toString(next());
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
    public int hashCode() {
        return Objects.hash(this.startTime, this.workerBits, this.dataCenterId, this.workerId);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof Snowflake) {
            final Snowflake s = (Snowflake) obj;
            match = s.startTime == this.startTime
                    && s.workerBits == this.workerBits
                    && s.dataCenterId == this.dataCenterId
                    && s.workerId == this.workerId;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public long getWorkerBits() {
        return this.workerBits;
    }

    @Override
    public String toString() {
        return String.format("[%s workerBits:%s,startTime:%s,dataCenterId:%s,workerId:%s]"
                , Snowflake.class.getName()
                , this.workerBits
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
                timestamp = SystemClock.now();
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

    private String suffixWithZero(long number) {
        String str = Long.toString(number % 10_0000);
        if (str.length() < 5) {
            char[] chars = new char[5 - str.length()];
            Arrays.fill(chars, '0');
            str = new String(chars) + str;
        }
        return str;
    }

    private static IllegalStateException clockMovedBackwards(long offset) {
        String m = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", offset);
        return new IllegalStateException(m);
    }


}
