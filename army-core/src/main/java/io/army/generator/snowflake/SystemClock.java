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
