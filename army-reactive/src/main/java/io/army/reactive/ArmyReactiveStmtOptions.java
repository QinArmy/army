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

package io.army.reactive;

import io.army.record.ResultStates;
import io.army.session.MultiStmtMode;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session._ArmyStmtOptions;

import javax.annotation.Nullable;
import java.util.function.Consumer;

abstract class ArmyReactiveStmtOptions extends _ArmyStmtOptions {


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


}
