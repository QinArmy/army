package io.army.session;

import io.army.session.record.ResultStates;

import java.util.function.Consumer;

public abstract class ArmyStmtOptions {


    protected ArmyStmtOptions() {
        throw new UnsupportedOperationException();
    }


    protected static abstract class ArmyStmtOption implements StmtOption {

        private final boolean parseBatchAsMultiStmt;

        private final boolean preferServerPrepare;

        private final int fetchSize;

        private final int frequency;

        private final int timeoutMillis;

        private final long startMills;

        private final MultiStmtMode multiStmtMode;

        private final Consumer<ResultStates> statesConsumer;


        public ArmyStmtOption(StmtOptionBuilderSpec<?> builder) {

            this.preferServerPrepare = builder.preferServerPrepare;
            this.parseBatchAsMultiStmt = builder.parseBatchAsMultiStmt;
            this.fetchSize = builder.fetchSize;
            this.frequency = builder.frequency;

            final int timeoutMillis = builder.timeoutMillis;
            this.timeoutMillis = timeoutMillis;
            if (timeoutMillis > 0) {
                this.startMills = System.currentTimeMillis();
            } else {
                this.startMills = 0L;
            }

            final MultiStmtMode mode = builder.multiStmtMode;
            this.multiStmtMode = mode == null ? MultiStmtMode.DEFAULT : mode;

            final Consumer<ResultStates> statesConsumer = builder.statesConsumer;
            this.statesConsumer = statesConsumer == null ? ResultStates.IGNORE_STATES : statesConsumer;


        }

        public ArmyStmtOption(ArmyStmtOption option, final int timeoutMillis, long startMills) {
            assert timeoutMillis > 0;
            assert startMills > -1L;

            this.preferServerPrepare = option.preferServerPrepare;
            this.parseBatchAsMultiStmt = option.parseBatchAsMultiStmt;
            this.fetchSize = option.fetchSize;
            this.frequency = option.frequency;

            this.timeoutMillis = timeoutMillis;
            this.startMills = startMills;
            this.statesConsumer = option.statesConsumer;
            this.multiStmtMode = option.multiStmtMode;
        }


        @Override
        public final boolean isPreferServerPrepare() {
            return this.preferServerPrepare;
        }

        @Override
        public final boolean isSupportTimeout() {
            return this.timeoutMillis > 0;
        }

        @Override
        public final boolean isParseBatchAsMultiStmt() {
            return this.parseBatchAsMultiStmt;
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
                throw new IllegalStateException("Don't support timeout");
            }
            final long restMills;
            restMills = timeoutMills - (System.currentTimeMillis() - this.startMills);
            if (restMills >= timeoutMills) {
                final String m = String.format("timeout is %s millis , but rest %s millis", timeoutMills, restMills);
                throw new TimeoutException(m, restMills);
            }
            return (int) restMills;
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

        private int frequency = -1;

        private int timeoutMillis = 0;

        private MultiStmtMode multiStmtMode = MultiStmtMode.DEFAULT;

        private Consumer<ResultStates> statesConsumer = ResultStates.IGNORE_STATES;

        private boolean parseBatchAsMultiStmt;

        private boolean preferServerPrepare = true;


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
        public final B stateConsumer(Consumer<ResultStates> consumer) {
            this.statesConsumer = consumer;
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


    protected static abstract class ArmyDefaultStmtOption implements StmtOption {

        public ArmyDefaultStmtOption() {
        }

        @Override
        public final boolean isPreferServerPrepare() {
            // default true
            return true;
        }

        @Override
        public final boolean isSupportTimeout() {
            // default false
            return false;
        }

        @Override
        public final boolean isParseBatchAsMultiStmt() {
            // default false
            return false;
        }

        @Override
        public final int restSeconds() throws TimeoutException {
            throw new IllegalStateException();
        }

        @Override
        public final int restMillSeconds() throws TimeoutException {
            throw new IllegalStateException();
        }

        @Override
        public final int fetchSize() {
            return 0;
        }

        @Override
        public final int frequency() {
            return -1;
        }

        @Override
        public final MultiStmtMode multiStmtMode() {
            return MultiStmtMode.DEFAULT;
        }

        @Override
        public final Consumer<ResultStates> stateConsumer() {
            return ResultStates.IGNORE_STATES;
        }


    } // ArmyDefaultStmtOption


}
