package io.army.generator.snowflake;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public final class SystemClock {


    public static long now() {
        return Holder.INSTANCE.nowMills.get();
    }

    private final AtomicLong nowMills;

    private SystemClock() {
        this.nowMills = new AtomicLong(System.currentTimeMillis());

    }

    private void updateNow() {
        this.nowMills.set(System.currentTimeMillis());
    }


    private static final class Holder {

        private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, SystemClock.class.getName());
            thread.setDaemon(true);
            return thread;
        });

        static final SystemClock INSTANCE;

        static {
            final SystemClock clock = new SystemClock();
            INSTANCE = clock;
            SCHEDULER.scheduleAtFixedRate(clock::updateNow, 1L, 1L, TimeUnit.MILLISECONDS);
        }

    }


}
