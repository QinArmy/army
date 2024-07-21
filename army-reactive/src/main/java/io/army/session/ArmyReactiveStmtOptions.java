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
import io.army.option.Option;
import io.army.result.ResultStates;
import io.army.transaction.TransactionInfo;

import java.util.function.Consumer;

abstract class ArmyReactiveStmtOptions extends StmtOptions {


    private ArmyReactiveStmtOptions() {
        throw new UnsupportedOperationException();
    }

    static final ReactiveStmtOption DEFAULT = new FinalDefaultOption();

    static ReactiveStmtOption.Builder builder() {
        return new ArmyOptionBuilder();
    }


    static ReactiveStmtOption fetchSize(final int value) {
        if (value > 0) {
            return new OnlyFetchSizeOption(value);
        }
        return DEFAULT;
    }


    static ReactiveStmtOption frequency(final int value) {
        if (value > -1) {
            return new OnlyFrequencyOption(value);
        }
        return DEFAULT;
    }

    static ReactiveStmtOption timeoutMillis(final int millis) {
        if (millis > 0) {
            return new OnlyTimeoutOption(millis, System.currentTimeMillis());
        }
        return DEFAULT;
    }

    static ReactiveStmtOption multiStmtMode(final @Nullable MultiStmtMode mode) {
        if (mode == null) {
            throw new NullPointerException("mode null");
        }
        if (mode != MultiStmtMode.DEFAULT) {
            return new OnlyMultiStmtModeOption(mode);
        }
        return DEFAULT;
    }

    static ReactiveStmtOption stateConsumer(final @Nullable Consumer<ResultStates> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer null");
        }
        if (consumer != ResultStates.IGNORE_STATES) {
            return new OnlyStateConsumerOption(consumer);
        }
        return DEFAULT;
    }

    static ReactiveStmtOption preferServerPrepare(final boolean prefer) {
        if (prefer) {
            return DEFAULT;
        }
        return OnlyPreferServerPrepareOption.INSTANCE;
    }


    static ReactiveStmtOption overrideTimeoutIfNeed(final ReactiveStmtOption option, final TransactionInfo info) {
        final Integer timeout;
        timeout = info.valueOf(Option.TIMEOUT_MILLIS);
        if (timeout == null || option instanceof TransactionOverrideOption) {
            return option;
        }
        final Long startMillis;
        startMillis = info.valueOf(Option.START_MILLIS);
        assert startMillis != null;

        final TransactionOverrideOption newOption;
        if (option == DEFAULT || option instanceof ReactiveTimeoutOption) {
            newOption = new OnlyTransactionTimeoutOption(timeout, startMillis);
        } else {
            newOption = new ArmyTransactionTimeoutOption(option, timeout, startMillis, option.stateConsumer());
        }
        return newOption;
    }


    static ReactiveStmtOption overrideOptionIfNeed(final ReactiveStmtOption option, final @Nullable TransactionInfo info,
                                                   final @Nullable Consumer<ResultStates> armyConsumer) {


        final Integer timeout;
        final Long startMillis;
        if (info == null) {
            timeout = null;
            startMillis = null;
        } else {
            timeout = info.valueOf(Option.TIMEOUT_MILLIS);
            startMillis = info.valueOf(Option.START_MILLIS);
            assert timeout == null || startMillis != null;
        }

        if (timeout == null && armyConsumer == null) {
            return option;
        }

        final Consumer<ResultStates> finalConsumer, consumerOfUser;
        consumerOfUser = option.stateConsumer();
        if (armyConsumer == null) {
            finalConsumer = consumerOfUser;
        } else if (consumerOfUser == ResultStates.IGNORE_STATES) {
            finalConsumer = armyConsumer;
        } else {
            finalConsumer = armyConsumer.andThen(consumerOfUser);
        }


        final ReactiveStmtOption newOption;
        if (timeout == null) {
            if (option == DEFAULT || option instanceof OnlyStateConsumerOption) {
                newOption = new OnlyStateConsumerOption(finalConsumer);
            } else {
                newOption = new ArmyReactiveOverrideOption(option, option.timeoutMillis(), option.startTimeMillis(), finalConsumer);
            }
        } else if (finalConsumer == ResultStates.IGNORE_STATES) {
            if (option == DEFAULT || option instanceof OnlyStateConsumerOption) {
                newOption = new OnlyTransactionTimeoutOption(timeout, startMillis);
            } else {
                newOption = new ArmyReactiveOverrideOption(option, timeout, startMillis, finalConsumer);
            }
        } else {
            newOption = new ArmyReactiveOverrideOption(option, timeout, startMillis, finalConsumer);
        }
        return newOption;
    }

    static ReactiveStmtOption replaceStateConsumer(final ReactiveStmtOption option, final Consumer<ResultStates> consumer) {
        final ReactiveStmtOption newOption;
        if (option == DEFAULT || option instanceof OnlyStateConsumerOption) {
            newOption = new OnlyStateConsumerOption(consumer);
        } else if (option.isSupportTimeout()) {
            newOption = new ArmyReactiveOverrideOption(option, option.timeoutMillis(), option.startTimeMillis(), consumer);
        } else {
            newOption = new ArmyReactiveOverrideOption(option, 0, 0L, consumer);
        }
        return newOption;
    }

    interface TransactionOverrideOption extends ReactiveStmtOption {

    }


    private static final class ArmyOptionBuilder extends StmtOptionBuilderSpec<ReactiveStmtOption.Builder>
            implements ReactiveStmtOption.Builder {

        private ArmyOptionBuilder() {
        }

        @Override
        public ReactiveStmtOption build() {
            return new ArmyReactiveOption(this);
        }

    } // ArmyOptionBuilder


    private static abstract class DefaultReactiveStmtOption extends DefaultStmtOption implements ReactiveStmtOption {

        private DefaultReactiveStmtOption() {
        }


    } // DefaultReactiveStmtOption

    private static final class FinalDefaultOption extends DefaultStmtOption implements ReactiveStmtOption {

        private FinalDefaultOption() {
        }


    } // FinalDefaultOption


    /**
     * <p>This class is base class of following :
     * <ul>
     *     <li>{@link OnlyTransactionTimeoutOption}</li>
     *     <li>{@link OnlyTimeoutOption}</li>
     * </ul>
     */
    private static abstract class ReactiveTimeoutOption extends ArmyDefaultTimeOutOption
            implements ReactiveStmtOption {

        private ReactiveTimeoutOption(int timeoutMillis, long startMills) {
            super(timeoutMillis, startMills);
        }

        private ReactiveTimeoutOption(ReactiveStmtOption option) {
            super(option);
        }

    } // ReactiveTimeoutOption


    private static final class OnlyTransactionTimeoutOption extends ReactiveTimeoutOption
            implements TransactionOverrideOption {

        private OnlyTransactionTimeoutOption(int timeoutMillis, long startMillis) {
            super(timeoutMillis, startMillis);
        }

        private OnlyTransactionTimeoutOption(ReactiveStmtOption option) {
            super(option);
        }

    } // OnlyTransactionTimeoutOption


    private static final class ArmyReactiveOption extends ArmyStmtOption implements ReactiveStmtOption {

        private ArmyReactiveOption(ArmyOptionBuilder builder) {
            super(builder);
        }


    } // ArmyReactiveOption

    private static final class ArmyTransactionTimeoutOption extends ArmyStmtOption
            implements TransactionOverrideOption {

        private ArmyTransactionTimeoutOption(ReactiveStmtOption option, int timeoutMillis, long startMills,
                                             Consumer<ResultStates> consumer) {
            super(option, timeoutMillis, startMills, consumer);
        }


    } // ArmyTransactionTimeoutOption


    private static final class OnlyFetchSizeOption extends DefaultReactiveStmtOption {

        private final int fetchSize;

        private OnlyFetchSizeOption(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        @Override
        public int fetchSize() {
            return this.fetchSize;
        }


    } // OnlyFetchSizeOption

    private static final class OnlyFrequencyOption extends DefaultReactiveStmtOption {

        private final int frequency;

        public OnlyFrequencyOption(int frequency) {
            this.frequency = frequency;
        }

        @Override
        public int frequency() {
            return this.frequency;
        }

    } // OnlyFrequencyOption

    private static final class OnlyTimeoutOption extends ReactiveTimeoutOption {


        private OnlyTimeoutOption(int timeoutMillis, long startMills) {
            super(timeoutMillis, startMills);
        }

    } // OnlyTimeoutOption


    private static final class OnlyMultiStmtModeOption extends DefaultReactiveStmtOption {

        private final MultiStmtMode multiStmtMode;

        private OnlyMultiStmtModeOption(MultiStmtMode mode) {
            this.multiStmtMode = mode;
        }

        @Override
        public MultiStmtMode multiStmtMode() {
            return this.multiStmtMode;
        }


    } // OnlyMultiStmtModeOption


    private static final class OnlyStateConsumerOption extends DefaultReactiveStmtOption {

        private final Consumer<ResultStates> statesConsumer;

        private OnlyStateConsumerOption(Consumer<ResultStates> statesConsumer) {
            this.statesConsumer = statesConsumer;
        }

        @Override
        public Consumer<ResultStates> stateConsumer() {
            return this.statesConsumer;
        }


    } // OnlyStateConsumerOption


    private static final class OnlyPreferServerPrepareOption extends DefaultReactiveStmtOption {

        private static final OnlyPreferServerPrepareOption INSTANCE = new OnlyPreferServerPrepareOption();

        /**
         * @see ArmyReactiveStmtOptions#preferServerPrepare(boolean)
         */
        private OnlyPreferServerPrepareOption() {
        }

        @Override
        public boolean isPreferServerPrepare() {
            return false;
        }

    } // OnlyPreferServerPrepareOption


    private static final class ArmyReactiveOverrideOption extends ArmyStmtOption
            implements TransactionOverrideOption {

        private ArmyReactiveOverrideOption(ReactiveStmtOption option, int timeoutMillis, long startMills,
                                           Consumer<ResultStates> consumer) {
            super(option, timeoutMillis, startMills, consumer);
        }


    } // ArmyReactiveOverrideOption


}
