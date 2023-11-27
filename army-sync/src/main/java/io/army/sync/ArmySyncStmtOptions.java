package io.army.sync;

import io.army.session.MultiStmtMode;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session._ArmyStmtOptions;
import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;

abstract class ArmySyncStmtOptions extends _ArmyStmtOptions {

    private ArmySyncStmtOptions() {
        throw new UnsupportedOperationException();
    }


    static final SyncStmtOption DEFAULT = new DefaultOption();

    static final boolean DEFAULT_PREFER_CLIENT_STREAM = true;

    static SyncStmtOption.Builder builder() {
        return new ArmyOptionBuilder();
    }


    static SyncStmtOption fetchSize(final int value) {
        if (value > 0) {
            return new OnlyFetchSizeOption(value);
        }
        return DEFAULT;
    }


    static SyncStmtOption frequency(final int value) {
        if (value > -1) {
            return new OnlyFrequencyOption(value);
        }
        return DEFAULT;
    }

    static SyncStmtOption splitSize(final int value) {
        if (value > 0) {
            return new OnlySplitSizeOption(value);
        }
        return DEFAULT;
    }

    static SyncStmtOption timeoutMillis(final int millis) {
        if (millis > 0) {
            return new OnlyTimeoutOption(millis);
        }
        return DEFAULT;
    }

    static SyncStmtOption multiStmtMode(final @Nullable MultiStmtMode mode) {
        if (mode == null) {
            throw new NullPointerException("mode null");
        }
        if (mode != MultiStmtMode.DEFAULT) {
            return new OnlyMultiStmtModeOption(mode);
        }
        return DEFAULT;
    }

    static SyncStmtOption stateConsumer(final @Nullable Consumer<ResultStates> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer null");
        }
        if (consumer != ResultStates.IGNORE_STATES) {
            return new OnlyStateConsumerOption(consumer);
        }
        return DEFAULT;
    }

    static SyncStmtOption commanderConsumer(final @Nullable Consumer<StreamCommander> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer null");
        }
        return new OnlyCommanderOption(consumer);
    }


    static SyncStmtOption overrideOptionIfNeed(final SyncStmtOption option, final TransactionInfo info) {
        final Integer timeout;
        timeout = info.valueOf(Option.TIMEOUT_MILLIS);
        if (timeout == null || option instanceof TransactionOverrideOption) {
            return option;
        }
        final Long startMillis;
        startMillis = info.valueOf(Option.START_MILLIS);
        assert startMillis != null;

        final TransactionOverrideOption newOption;
        if (option == DEFAULT || option instanceof OnlyTimeoutOption) {
            newOption = new OnlyTransactionTimeoutOption(timeout, startMillis);
        } else {
            newOption = new ArmySyncOverrideOption(option, timeout, startMillis);
        }
        return newOption;
    }

    static SyncStmtOption replaceStateConsumer(final SyncStmtOption option, final Consumer<ResultStates> consumer) {
        final SyncStmtOption newOption;
        if (option == DEFAULT || option instanceof OnlyStateConsumerOption) {
            newOption = new OnlyStateConsumerOption(consumer);
        } else {
            newOption = new ArmySyncOverrideOption(option, consumer);
        }
        return newOption;
    }


    interface TransactionOverrideOption extends SyncStmtOption {

    }

    private static final class OnlyTransactionTimeoutOption extends ArmyOnlyTimeoutOption
            implements TransactionOverrideOption {

        private OnlyTransactionTimeoutOption(int timeoutMillis, long startMillis) {
            super(timeoutMillis, startMillis);
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }

    } // OnlyTransactionTimeoutOption


    private static final class ArmyOptionBuilder extends StmtOptionBuilderSpec<SyncStmtOption.Builder>
            implements SyncStmtOption.Builder {

        private int splitSize;

        private Consumer<StreamCommander> commanderConsumer;

        private boolean preferClientStream = DEFAULT_PREFER_CLIENT_STREAM;

        private ArmyOptionBuilder() {
        }

        @Override
        public SyncStmtOption.Builder splitSize(int value) {
            this.splitSize = value;
            return this;
        }

        @Override
        public SyncStmtOption.Builder commanderConsumer(Consumer<StreamCommander> consumer) {
            this.commanderConsumer = consumer;
            return this;
        }

        @Override
        public SyncStmtOption.Builder preferClientStream(boolean yes) {
            this.preferClientStream = yes;
            return this;
        }

        @Override
        public SyncStmtOption build() {
            return new ArmySyncOption(this);
        }

    } // ArmyOptionBuilder


    private static abstract class ArmySyncStmtStreamOption extends ArmyStmtOption implements SyncStmtOption {


        private final int splitSize;

        private final Consumer<StreamCommander> commanderConsumer;

        private final boolean preferClientStream;

        private ArmySyncStmtStreamOption(ArmyOptionBuilder builder) {
            super(builder);
            this.splitSize = builder.splitSize;
            this.commanderConsumer = builder.commanderConsumer;
            this.preferClientStream = builder.preferClientStream;
        }

        private ArmySyncStmtStreamOption(final SyncStmtOption option, int timeoutMillis, long startMills) {
            super(option, timeoutMillis, startMills);

            if (option instanceof ArmySyncStmtStreamOption) {
                final ArmySyncStmtStreamOption syncOption = (ArmySyncStmtStreamOption) option;
                this.splitSize = syncOption.splitSize;
                this.commanderConsumer = syncOption.commanderConsumer;
                this.preferClientStream = syncOption.preferClientStream;
            } else {
                this.splitSize = option.splitSize();
                this.commanderConsumer = option.commanderConsumer();
                this.preferClientStream = option.isPreferClientStream();
            }
        }

        private ArmySyncStmtStreamOption(SyncStmtOption option, Consumer<ResultStates> statesConsumer) {
            super(option, statesConsumer);
            if (option instanceof ArmySyncStmtStreamOption) {
                final ArmySyncStmtStreamOption syncOption = (ArmySyncStmtStreamOption) option;
                this.splitSize = syncOption.splitSize;
                this.commanderConsumer = syncOption.commanderConsumer;
                this.preferClientStream = syncOption.preferClientStream;
            } else {
                this.splitSize = option.splitSize();
                this.commanderConsumer = option.commanderConsumer();
                this.preferClientStream = option.isPreferClientStream();
            }
        }

        @Nullable
        @Override
        public final Consumer<StreamCommander> commanderConsumer() {
            return this.commanderConsumer;
        }

        @Override
        public final int splitSize() {
            return this.splitSize;
        }

        @Override
        public final boolean isPreferClientStream() {
            return this.preferClientStream;
        }


    } // ArmySyncStmtStreamOption


    private static final class ArmySyncOption extends ArmySyncStmtStreamOption {

        private ArmySyncOption(ArmyOptionBuilder builder) {
            super(builder);
        }

    } // ArmyReactiveOption

    private static final class ArmySyncOverrideOption extends ArmySyncStmtStreamOption
            implements TransactionOverrideOption {

        private ArmySyncOverrideOption(SyncStmtOption option, int timeoutMillis, long startMills) {
            super(option, timeoutMillis, startMills);
        }


        private ArmySyncOverrideOption(SyncStmtOption option, Consumer<ResultStates> statesConsumer) {
            super(option, statesConsumer);
        }

    } // ArmySyncOverrideOption


    private static final class DefaultOption extends ArmyDefaultStmtOption implements SyncStmtOption {

        private DefaultOption() {
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }


    } // DefaultOption

    private static final class OnlyFetchSizeOption extends ArmyOnlyFetchSizeOption implements SyncStmtOption {

        private OnlyFetchSizeOption(int fetchSize) {
            super(fetchSize);
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }

    } // OnlyFetchSizeOption

    private static final class OnlyFrequencyOption extends ArmyOnlyFrequencyOption implements SyncStmtOption {

        private OnlyFrequencyOption(int frequency) {
            super(frequency);
        }


        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }

    } // OnlyFrequencyOption

    private static final class OnlyTimeoutOption extends ArmyOnlyTimeoutOption implements SyncStmtOption {

        private OnlyTimeoutOption(int timeoutMillis) {
            super(timeoutMillis);
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }


    } // OnlyTimeoutOption


    private static final class OnlyMultiStmtModeOption extends ArmyOnlyMultiStmtModeOption
            implements SyncStmtOption {

        private OnlyMultiStmtModeOption(MultiStmtMode mode) {
            super(mode);
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }

    } // OnlyMultiStmtModeOption


    private static final class OnlyStateConsumerOption extends ArmyOnlyStateConsumerOption
            implements SyncStmtOption {

        private OnlyStateConsumerOption(Consumer<ResultStates> statesConsumer) {
            super(statesConsumer);
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }


    } // OnlyStateConsumerOption


    private static final class OnlyCommanderOption extends ArmyDefaultStmtOption implements SyncStmtOption {

        private final Consumer<StreamCommander> commanderConsumer;

        private OnlyCommanderOption(Consumer<StreamCommander> commanderConsumer) {
            this.commanderConsumer = commanderConsumer;
        }


        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return this.commanderConsumer;
        }

        @Override
        public int splitSize() {
            return 0;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }

    } // OnlyCommanderOption


    private static final class OnlySplitSizeOption extends ArmyDefaultStmtOption implements SyncStmtOption {

        private final int splitSize;

        private OnlySplitSizeOption(int splitSize) {
            this.splitSize = splitSize;
        }

        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return splitSize;
        }

        @Override
        public boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }


    } // OnlySplitSizeOption


}
