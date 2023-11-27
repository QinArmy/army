package io.army.reactive;

import io.army.session.MultiStmtMode;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session._ArmyStmtOptions;
import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;

abstract class ArmyReactiveStmtOptions extends _ArmyStmtOptions {


    private ArmyReactiveStmtOptions() {
        throw new UnsupportedOperationException();
    }

    static final ReactiveStmtOption DEFAULT = new DefaultOption();

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
            return new OnlyTimeoutOption(millis);
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


    static ReactiveStmtOption overrideOptionIfNeed(final ReactiveStmtOption option, final TransactionInfo info) {
        final Integer timeout;
        timeout = info.valueOf(Option.TIMEOUT);
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
            newOption = new ArmyReactiveOverrideOption(option, timeout, startMillis);
        }
        return newOption;
    }


    interface TransactionOverrideOption extends ReactiveStmtOption {

    }

    private static final class OnlyTransactionTimeoutOption extends ArmyOnlyTimeoutOption
            implements TransactionOverrideOption {

        private OnlyTransactionTimeoutOption(int timeoutMillis, long startMillis) {
            super(timeoutMillis, startMillis);
        }

    } // OnlyTransactionTimeoutOption


    private static final class ArmyOptionBuilder extends StmtOptionBuilderSpec<ReactiveStmtOption.Builder>
            implements ReactiveStmtOption.Builder {

        private ArmyOptionBuilder() {
        }

        @Override
        public ReactiveStmtOption build() {
            return new ArmyReactiveOption(this);
        }

    } // ArmyOptionBuilder


    private static final class ArmyReactiveOption extends ArmyStmtOption implements ReactiveStmtOption {

        private ArmyReactiveOption(ArmyOptionBuilder builder) {
            super(builder);
        }


    } // ArmyReactiveOption

    private static final class ArmyReactiveOverrideOption extends ArmyStmtOption implements TransactionOverrideOption {

        private ArmyReactiveOverrideOption(ReactiveStmtOption option, int timeoutMillis, long startMills) {
            super(option, timeoutMillis, startMills);
        }

    } // ArmyReactiveOverrideOption


    private static final class DefaultOption extends ArmyDefaultStmtOption implements ReactiveStmtOption {

        private DefaultOption() {
        }


    } // DefaultOption

    private static final class OnlyFetchSizeOption extends ArmyOnlyFetchSizeOption implements ReactiveStmtOption {

        private OnlyFetchSizeOption(int fetchSize) {
            super(fetchSize);
        }

    } // OnlyFetchSizeOption

    private static final class OnlyFrequencyOption extends ArmyOnlyFrequencyOption implements ReactiveStmtOption {

        public OnlyFrequencyOption(int frequency) {
            super(frequency);
        }


    } // OnlyFrequencyOption

    private static final class OnlyTimeoutOption extends ArmyOnlyTimeoutOption implements ReactiveStmtOption {

        private OnlyTimeoutOption(int timeoutMillis) {
            super(timeoutMillis);
        }


    } // OnlyTimeoutOption


    private static final class OnlyMultiStmtModeOption extends ArmyOnlyMultiStmtModeOption
            implements ReactiveStmtOption {

        private OnlyMultiStmtModeOption(MultiStmtMode mode) {
            super(mode);
        }

    } // OnlyMultiStmtModeOption


    private static final class OnlyStateConsumerOption extends ArmyOnlyStateConsumerOption
            implements ReactiveStmtOption {

        private OnlyStateConsumerOption(Consumer<ResultStates> statesConsumer) {
            super(statesConsumer);
        }

    } // OnlyStateConsumerOption


}
