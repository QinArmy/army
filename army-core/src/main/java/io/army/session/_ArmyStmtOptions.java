package io.army.session;

import io.army.session.record.ResultStates;

import java.util.function.Consumer;

public abstract class _ArmyStmtOptions {


    protected _ArmyStmtOptions() {
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

        public ArmyStmtOption(final StmtOption option, final int timeoutMillis, long startMills) {
            assert timeoutMillis > 0;
            assert startMills > -1L;

            this.timeoutMillis = timeoutMillis;
            this.startMills = startMills;

            if (option instanceof ArmyStmtOption) {
                final ArmyStmtOption armyOption = (ArmyStmtOption) option;

                this.preferServerPrepare = armyOption.preferServerPrepare;
                this.parseBatchAsMultiStmt = armyOption.parseBatchAsMultiStmt;
                this.fetchSize = armyOption.fetchSize;
                this.frequency = armyOption.frequency;

                this.statesConsumer = armyOption.statesConsumer;
                this.multiStmtMode = armyOption.multiStmtMode;
            } else {
                this.preferServerPrepare = option.isPreferServerPrepare();
                this.parseBatchAsMultiStmt = option.isParseBatchAsMultiStmt();
                this.fetchSize = option.fetchSize();
                this.frequency = option.frequency();

                this.statesConsumer = option.stateConsumer();
                this.multiStmtMode = option.multiStmtMode();
            }

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

    private static abstract class ArmyPreferOption implements StmtOption {

        private ArmyPreferOption() {
        }

        @Override
        public final boolean isPreferServerPrepare() {
            // default true, prefer server prepare
            return true;
        }

        @Override
        public final boolean isParseBatchAsMultiStmt() {
            // default false
            return false;
        }


    } // ArmyPreferOption


    private static abstract class ArmyDefaultTimeoutOption extends ArmyPreferOption {

        private ArmyDefaultTimeoutOption() {
        }

        @Override
        public final boolean isSupportTimeout() {
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


    } // ArmyDefaultTimeoutOption


    protected static abstract class ArmyDefaultStmtOption extends ArmyDefaultTimeoutOption {

        public ArmyDefaultStmtOption() {
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


    protected static abstract class ArmyOnlyFetchSizeOption extends ArmyDefaultTimeoutOption {

        private final int fetchSize;

        public ArmyOnlyFetchSizeOption(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        @Override
        public final int fetchSize() {
            return this.fetchSize;
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


    } // ArmyOnlyFetchSizeOption


    protected static abstract class ArmyOnlyFrequencyOption extends ArmyDefaultTimeoutOption {

        private final int frequency;

        public ArmyOnlyFrequencyOption(int frequency) {
            this.frequency = frequency;
        }

        @Override
        public final int fetchSize() {
            return 0;
        }

        @Override
        public final int frequency() {
            return this.frequency;
        }

        @Override
        public final MultiStmtMode multiStmtMode() {
            return MultiStmtMode.DEFAULT;
        }

        @Override
        public final Consumer<ResultStates> stateConsumer() {
            return ResultStates.IGNORE_STATES;
        }


    } // ArmyOnlyFrequencyOption

    protected static abstract class ArmyOnlyMultiStmtModeOption extends ArmyDefaultTimeoutOption {

        private final MultiStmtMode mode;

        public ArmyOnlyMultiStmtModeOption(MultiStmtMode mode) {
            this.mode = mode;
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
            return mode;
        }

        @Override
        public final Consumer<ResultStates> stateConsumer() {
            return ResultStates.IGNORE_STATES;
        }


    } // ArmyOnlyMultiStmtModeOption


    protected static abstract class ArmyOnlyStateConsumerOption extends ArmyDefaultTimeoutOption {

        private final Consumer<ResultStates> statesConsumer;

        public ArmyOnlyStateConsumerOption(Consumer<ResultStates> statesConsumer) {
            this.statesConsumer = statesConsumer;
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
            return this.statesConsumer;
        }


    } // ArmyOnlyStateConsumerOption

    protected static abstract class ArmyOnlyTimeoutOption extends ArmyPreferOption {

        private final int timeoutMillis;

        private final long startMillis;

        public ArmyOnlyTimeoutOption(int timeoutMillis) {
            assert timeoutMillis > 0;
            this.timeoutMillis = timeoutMillis;
            this.startMillis = System.currentTimeMillis();
        }

        public ArmyOnlyTimeoutOption(int timeoutMillis, long startMillis) {
            assert timeoutMillis > 0;
            assert startMillis > -1L;
            this.timeoutMillis = timeoutMillis;
            this.startMillis = startMillis;
        }

        @Override
        public final boolean isSupportTimeout() {
            return this.timeoutMillis > 0;
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
            restMills = timeoutMills - (System.currentTimeMillis() - this.startMillis);
            if (restMills >= timeoutMills) {
                final String m = String.format("timeout is %s millis , but rest %s millis", timeoutMills, restMills);
                throw new TimeoutException(m, restMills);
            }
            return (int) restMills;
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


    } // ArmyOnlyTimeoutOption


}
