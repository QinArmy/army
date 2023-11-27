package io.army.reactive;

import io.army.session.MultiStmtMode;
import io.army.session.StmtOption;
import io.army.session.record.ResultStates;

import java.util.function.Consumer;

public interface ReactiveStmtOption extends StmtOption {


    static ReactiveStmtOption fetchSize(int value) {
        return ArmyReactiveStmtOptions.fetchSize(value);
    }


    static ReactiveStmtOption frequency(int value) {
        return ArmyReactiveStmtOptions.frequency(value);
    }

    static ReactiveStmtOption timeoutMillis(int millis) {
        return ArmyReactiveStmtOptions.timeoutMillis(millis);
    }

    static ReactiveStmtOption multiStmtMode(MultiStmtMode mode) {
        return ArmyReactiveStmtOptions.multiStmtMode(mode);
    }

    static ReactiveStmtOption stateConsumer(Consumer<ResultStates> consumer) {
        return ArmyReactiveStmtOptions.stateConsumer(consumer);
    }

    static Builder builder() {
        return ArmyReactiveStmtOptions.builder();
    }


    interface Builder extends StmtOption.BuilderSpec<Builder> {

        ReactiveStmtOption build();

    }


}
