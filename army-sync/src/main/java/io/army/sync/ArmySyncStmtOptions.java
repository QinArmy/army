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


    static final SyncStmtOption DEFAULT = new FinalDefaultOption();

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
        if (value > DEFAULT_FREQUENCY) {
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
            return new OnlyTimeoutOption(millis, System.currentTimeMillis());
        }
        return DEFAULT;
    }

    static SyncStmtOption multiStmtMode(final @Nullable MultiStmtMode mode) {
        if (mode == null) {
            throw new NullPointerException("MultiStmtMode must non-null");
        }
        if (mode != MultiStmtMode.DEFAULT) {
            return new OnlyMultiStmtModeOption(mode);
        }
        return DEFAULT;
    }

    static SyncStmtOption stateConsumer(final @Nullable Consumer<ResultStates> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer must non-null");
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
        if ((timeout == null || option instanceof TransactionOverrideOption)) {
            return option;
        }
        final Long startMillis;
        startMillis = info.valueOf(Option.START_MILLIS);
        assert startMillis != null;

        final TransactionOverrideOption newOption;
        if ((option == DEFAULT || option instanceof OnlyTimeoutOption)) {
            newOption = new OnlyTransactionTimeoutOption(timeout, startMillis);
        } else {
            newOption = new ArmySyncOverrideOption(option, timeout, startMillis, option.stateConsumer());
        }
        return newOption;
    }

    /**
     * @param validator optimistic lock validator
     */
    static SyncStmtOption overrideOptionWithOptimisticLockIfNeed(final SyncStmtOption option,
                                                                 final Consumer<ResultStates> validator,
                                                                 final @Nullable TransactionInfo info) {
        final Consumer<ResultStates> consumerOfUser, consumer;
        consumerOfUser = option.stateConsumer();
        if (consumerOfUser == ResultStates.IGNORE_STATES) {
            consumer = validator;
        } else {
            consumer = validator.andThen(consumerOfUser);
        }

        final Integer timeout;

        if (info == null || (timeout = info.valueOf(Option.TIMEOUT_MILLIS)) == null) {
            return replaceStateConsumer(option, consumer);
        }

        final Long startMillis;
        startMillis = info.valueOf(Option.START_MILLIS);
        assert startMillis != null;

        final SyncStmtOption newOption;
        if ((option == DEFAULT || option instanceof SyncTimeoutOption)) {
            newOption = new TimeoutAndStateConsumerOption(timeout, startMillis, consumer);
        } else {
            newOption = new ArmySyncOverrideOption(option, timeout, startMillis, consumer);
        }
        return newOption;
    }

    static SyncStmtOption replaceStateConsumer(final SyncStmtOption option, final Consumer<ResultStates> consumer) {
        final SyncStmtOption newOption;
        if (option == DEFAULT || option instanceof OnlyStateConsumerOption) {
            newOption = new OnlyStateConsumerOption(consumer);
        } else if (option.isSupportTimeout()) {
            newOption = new ArmySyncOverrideOption(option, option.timeoutMillis(), option.startTimeMillis(), consumer);
        } else {
            newOption = new ArmySyncOverrideOption(option, 0, 0L, consumer);
        }
        return newOption;
    }


    interface TransactionOverrideOption extends SyncStmtOption {

    }


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


    private static abstract class ArmySyncStmtOption extends ArmyStmtOption implements SyncStmtOption {

        private final int splitSize;

        private final Consumer<StreamCommander> commanderConsumer;

        private final boolean preferClientStream;

        private ArmySyncStmtOption(ArmyOptionBuilder builder) {
            super(builder);
            this.splitSize = builder.splitSize;
            this.commanderConsumer = builder.commanderConsumer;
            this.preferClientStream = builder.preferClientStream;
        }

        private ArmySyncStmtOption(final SyncStmtOption option, int timeoutMillis, long startMills,
                                   Consumer<ResultStates> consumer) {
            super(option, timeoutMillis, startMills, consumer);

            if (option instanceof ArmySyncStmtOption) {
                final ArmySyncStmtOption syncOption = (ArmySyncStmtOption) option;
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

    private static abstract class DefaultSyncStmtOption extends DefaultStmtOption implements SyncStmtOption {

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

    } // DefaultSyncStmtOption

    private static final class ArmySyncOption extends ArmySyncStmtOption {

        private ArmySyncOption(ArmyOptionBuilder builder) {
            super(builder);
        }

    } // ArmyReactiveOption


    private static final class ArmySyncOverrideOption extends ArmySyncStmtOption
            implements TransactionOverrideOption {

        private ArmySyncOverrideOption(SyncStmtOption option, int timeoutMillis, long startMills,
                                       Consumer<ResultStates> consumer) {
            super(option, timeoutMillis, startMills, consumer);
        }


    } // ArmySyncOverrideOption


    private static final class FinalDefaultOption extends DefaultSyncStmtOption {

        private FinalDefaultOption() {
        }

    } // FinalDefaultOption


    private static final class OnlyFetchSizeOption extends DefaultSyncStmtOption {

        private final int fetchSize;

        private OnlyFetchSizeOption(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        @Override
        public int fetchSize() {
            return this.fetchSize;
        }


    } // OnlyFetchSizeOption

    private static final class OnlyFrequencyOption extends DefaultSyncStmtOption {

        private final int frequency;

        private OnlyFrequencyOption(int frequency) {
            this.frequency = frequency;
        }

        @Override
        public int frequency() {
            return this.frequency;
        }

    } // OnlyFrequencyOption


    private static abstract class SyncTimeoutOption extends ArmyDefaultTimeOutOption implements SyncStmtOption {

        private SyncTimeoutOption(int timeoutMillis, long startMills) {
            super(timeoutMillis, startMills);
        }

        @Nullable
        @Override
        public final Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public final int splitSize() {
            return 0;
        }

        @Override
        public final boolean isPreferClientStream() {
            return DEFAULT_PREFER_CLIENT_STREAM;
        }

    } // SyncTimeoutOption

    private static class OnlyTimeoutOption extends SyncTimeoutOption {

        private OnlyTimeoutOption(int timeoutMillis, long startMills) {
            super(timeoutMillis, startMills);
        }


    } // OnlyTimeoutOption


    private static final class OnlyTransactionTimeoutOption extends SyncTimeoutOption
            implements TransactionOverrideOption {

        private OnlyTransactionTimeoutOption(int timeoutMillis, long startMillis) {
            super(timeoutMillis, startMillis);
        }

    } // OnlyTransactionTimeoutOption


    private static final class TimeoutAndStateConsumerOption extends SyncTimeoutOption {

        private final Consumer<ResultStates> statesConsumer;

        private TimeoutAndStateConsumerOption(int timeoutMillis, long startMills, Consumer<ResultStates> statesConsumer) {
            super(timeoutMillis, startMills);
            this.statesConsumer = statesConsumer;
        }


        @Override
        public Consumer<ResultStates> stateConsumer() {
            return this.statesConsumer;
        }


    } // TimeoutAndStateConsumerOption


    private static final class OnlyMultiStmtModeOption extends DefaultSyncStmtOption {

        private final MultiStmtMode multiStmtMode;

        private OnlyMultiStmtModeOption(MultiStmtMode mode) {
            this.multiStmtMode = mode;
        }

        @Override
        public MultiStmtMode multiStmtMode() {
            return this.multiStmtMode;
        }


    } // OnlyMultiStmtModeOption


    private static final class OnlyStateConsumerOption extends DefaultSyncStmtOption {

        private final Consumer<ResultStates> statesConsumer;

        private OnlyStateConsumerOption(Consumer<ResultStates> statesConsumer) {
            this.statesConsumer = statesConsumer;
        }

        @Override
        public Consumer<ResultStates> stateConsumer() {
            return this.statesConsumer;
        }

    } // OnlyStateConsumerOption


    private static final class OnlyCommanderOption extends DefaultSyncStmtOption {

        private final Consumer<StreamCommander> commanderConsumer;

        private OnlyCommanderOption(Consumer<StreamCommander> commanderConsumer) {
            this.commanderConsumer = commanderConsumer;
        }


        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return this.commanderConsumer;
        }


    } // OnlyCommanderOption


    private static final class OnlySplitSizeOption extends DefaultSyncStmtOption {

        private final int splitSize;

        private OnlySplitSizeOption(int splitSize) {
            this.splitSize = splitSize;
        }

        @Override
        public int splitSize() {
            return splitSize;
        }


    } // OnlySplitSizeOption


}
