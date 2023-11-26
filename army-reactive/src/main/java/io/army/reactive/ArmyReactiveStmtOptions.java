package io.army.reactive;

import io.army.session.ArmyStmtOptions;

abstract class ArmyReactiveStmtOptions extends ArmyStmtOptions {


    private ArmyReactiveStmtOptions() {
        throw new UnsupportedOperationException();
    }

    static ReactiveStmtOption.Builder builder() {
        return new ArmyOptionBuilder();
    }

    static final ReactiveStmtOption DEFAULT = new DefaultOption();


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

        private ArmyReactiveOption(ArmyReactiveOption option, int timeoutMillis, long startMills) {
            super(option, timeoutMillis, startMills);
        }


    } // ArmyReactiveOption


    private static final class DefaultOption extends ArmyDefaultStmtOption implements ReactiveStmtOption {

        private DefaultOption() {
        }


    } // DefaultOption


}
