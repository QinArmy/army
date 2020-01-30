package io.army.generator.snowflake;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class SystemClock {

    private static class Holder {

        private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "SystemClock");
            thread.setDaemon(true);
            return thread;
        });

        private static final SystemClock INSTANCE = new SystemClock();

    }

    private final AtomicLong now;

    private SystemClock() {
        this.now = new AtomicLong(System.currentTimeMillis());
        Holder.SCHEDULER.scheduleAtFixedRate(
                () -> now.set(System.currentTimeMillis()), 1L, 1L, TimeUnit.MILLISECONDS);
    }

    public static long now() {
        return Holder.INSTANCE.now.get();
    }


}
