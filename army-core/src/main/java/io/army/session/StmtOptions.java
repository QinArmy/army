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

package io.army.session;

import io.army.lang.Nullable;
import io.army.option.MultiStmtMode;
import io.army.result.ResultStates;

import java.util.function.Consumer;

public abstract class StmtOptions {


    protected StmtOptions() {
        throw new UnsupportedOperationException();
    }

    protected static final boolean DEFAULT_PREFER_SERVER_PREPARE = true;

    protected static final boolean DEFAULT_PARSE_BATCH_AS_MULTI_STMT = false;

    protected static final byte DEFAULT_FREQUENCY = -1;


    private static IllegalStateException dontSupportTimeout() {
        return new IllegalStateException("Don't support timeout");
    }


    protected static abstract class DefaultStmtOption implements StmtOption {

        public DefaultStmtOption() {
        }

        @Override
        public boolean isPreferServerPrepare() {
            return DEFAULT_PREFER_SERVER_PREPARE;
        }

        @Override
        public boolean isParseBatchAsMultiStmt() {
            return DEFAULT_PARSE_BATCH_AS_MULTI_STMT;
        }

        @Override
        public int frequency() {
            return DEFAULT_FREQUENCY;
        }


        @Override
        public int fetchSize() {
            return 0;
        }

        @Override
        public MultiStmtMode multiStmtMode() {
            return MultiStmtMode.DEFAULT;
        }

        @Override
        public Consumer<ResultStates> stateConsumer() {
            return ResultStates.IGNORE_STATES;
        }


        @Override
        public boolean isSupportTimeout() {
            return false;
        }

        @Override
        public int timeoutMillis() {
            return 0;
        }

        @Override
        public long startTimeMillis() {
            return 0L;
        }

        @Override
        public int restSeconds() throws TimeoutException {
            throw dontSupportTimeout();
        }

        @Override
        public int restMillSeconds() throws TimeoutException {
            throw dontSupportTimeout();
        }


    } // DefaultStmtOption


    protected static abstract class ArmyDefaultTimeOutOption extends DefaultStmtOption {

        private final int timeoutMillis;

        private final long startMills;

        public ArmyDefaultTimeOutOption(int timeoutMillis, long startMills) {
            this.timeoutMillis = timeoutMillis;
            this.startMills = startMills;
        }

        public ArmyDefaultTimeOutOption(final StmtOption option) {
            if (option instanceof ArmyDefaultTimeOutOption) {
                this.timeoutMillis = ((ArmyDefaultTimeOutOption) option).timeoutMillis;
                this.startMills = ((ArmyDefaultTimeOutOption) option).startMills;
            } else if (option.isSupportTimeout()) {
                this.timeoutMillis = option.timeoutMillis();
                this.startMills = option.startTimeMillis();
            } else {
                this.timeoutMillis = 0;
                this.startMills = 0L;
            }
        }


        @Override
        public final boolean isSupportTimeout() {
            return this.timeoutMillis > 0;
        }

        @Override
        public final int timeoutMillis() {
            return this.timeoutMillis;
        }

        @Override
        public final long startTimeMillis() {
            return this.startMills;
        }

        @Override
        public final int restSeconds() throws TimeoutException {
            int seconds;
            seconds = restMillSeconds() * 1000;
            if (seconds < 1) {
                seconds = 1;
            }
            return seconds;
        }

        @Override
        public final int restMillSeconds() throws TimeoutException {
            final int timeoutMills = this.timeoutMillis;
            if (timeoutMills < 1) {
                throw dontSupportTimeout();
            }
            final long restMills;
            restMills = timeoutMills - (System.currentTimeMillis() - this.startMills);
            if (restMills >= timeoutMills) {
                final String m = String.format("timeout is %s millis , but rest %s millis", timeoutMills, restMills);
                throw new TimeoutException(m, restMills);
            }
            return (int) restMills;
        }


    } // ArmyTimeoutStmtOption


    protected static abstract class ArmyStmtOption extends ArmyDefaultTimeOutOption {

        private final boolean parseBatchAsMultiStmt;

        private final boolean preferServerPrepare;

        private final int fetchSize;

        private final int frequency;

        private final MultiStmtMode multiStmtMode;

        private final Consumer<ResultStates> statesConsumer;


        public ArmyStmtOption(StmtOptionBuilderSpec<?> builder) {
            super(builder.timeoutMillis, (builder.timeoutMillis > 0 ? System.currentTimeMillis() : 0L));

            this.preferServerPrepare = builder.preferServerPrepare;
            this.parseBatchAsMultiStmt = builder.parseBatchAsMultiStmt;
            this.fetchSize = builder.fetchSize;
            this.frequency = builder.frequency;

            final MultiStmtMode mode = builder.multiStmtMode;
            this.multiStmtMode = mode == null ? MultiStmtMode.DEFAULT : mode;

            final Consumer<ResultStates> statesConsumer = builder.statesConsumer;
            this.statesConsumer = statesConsumer == null ? ResultStates.IGNORE_STATES : statesConsumer;

        }

        public ArmyStmtOption(final StmtOption option, final int timeoutMillis, final long startMills,
                              final Consumer<ResultStates> statesConsumer, int fetchSize) {
            super(timeoutMillis, startMills);

            if (option instanceof ArmyStmtOption armyOption) {

                this.preferServerPrepare = armyOption.preferServerPrepare;
                this.parseBatchAsMultiStmt = armyOption.parseBatchAsMultiStmt;
                this.frequency = armyOption.frequency;

                this.multiStmtMode = armyOption.multiStmtMode;
            } else {
                this.preferServerPrepare = option.isPreferServerPrepare();
                this.parseBatchAsMultiStmt = option.isParseBatchAsMultiStmt();
                this.frequency = option.frequency();

                this.multiStmtMode = option.multiStmtMode();

            }
            this.fetchSize = fetchSize;
            this.statesConsumer = statesConsumer;

        }


        @Override
        public final boolean isPreferServerPrepare() {
            return this.preferServerPrepare;
        }

        @Override
        public final boolean isParseBatchAsMultiStmt() {
            return this.parseBatchAsMultiStmt;
        }

        @Override
        public final int fetchSize() {
            return this.fetchSize;
        }

        @Override
        public final int frequency() {
            return this.frequency;
        }

        @Override
        public final MultiStmtMode multiStmtMode() {
            return this.multiStmtMode;
        }

        @Override
        public final Consumer<ResultStates> stateConsumer() {
            return this.statesConsumer;
        }


    } // ArmyStmtOption


    @SuppressWarnings("unchecked")
    protected static abstract class StmtOptionBuilderSpec<B>
            implements StmtOption.BuilderSpec<B> {

        private int fetchSize = 0;

        private int frequency = DEFAULT_FREQUENCY;

        private int timeoutMillis = 0;

        private MultiStmtMode multiStmtMode = MultiStmtMode.DEFAULT;

        private Consumer<ResultStates> statesConsumer = ResultStates.IGNORE_STATES;

        private boolean parseBatchAsMultiStmt = DEFAULT_PARSE_BATCH_AS_MULTI_STMT;

        private boolean preferServerPrepare = DEFAULT_PREFER_SERVER_PREPARE;


        public StmtOptionBuilderSpec() {
        }

        @Override
        public final B fetchSize(int value) {
            this.fetchSize = value;
            return (B) this;
        }

        @Override
        public final B frequency(int value) {
            this.frequency = value;
            return (B) this;
        }

        @Override
        public final B timeoutMillis(int millis) {
            this.timeoutMillis = millis;
            return (B) this;
        }

        @Override
        public final B multiStmtMode(MultiStmtMode mode) {
            this.multiStmtMode = mode;
            return (B) this;
        }

        @Override
        public final B stateConsumer(final @Nullable Consumer<ResultStates> consumer) {
            this.statesConsumer = consumer == null ? ResultStates.IGNORE_STATES : consumer;
            return (B) this;
        }

        @Override
        public final B parseBatchAsMultiStmt(boolean yes) {
            this.parseBatchAsMultiStmt = yes;
            return (B) this;
        }

        @Override
        public final B preferServerPrepare(boolean yes) {
            this.preferServerPrepare = yes;
            return (B) this;
        }


    } // StmtOptionBuilder


}
