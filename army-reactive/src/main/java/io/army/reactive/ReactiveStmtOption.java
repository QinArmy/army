package io.army.reactive;

import io.army.session.MultiStmtMode;
import io.army.session.StmtOption;
import io.army.session.record.ResultStates;

import java.util.function.Consumer;

public interface ReactiveStmtOption extends StmtOption {


    static ReactiveStmtOption fetchSize(int value) {
        return null;
    }


    static ReactiveStmtOption frequency(int value) {
        return null;
    }

    static ReactiveStmtOption timeoutMillis(int millis) {
        return null;
    }

    static ReactiveStmtOption multiStmtMode(MultiStmtMode mode) {
        return null;
    }

    static ReactiveStmtOption stateConsumer(Consumer<ResultStates> consumer) {
        return null;
    }

    static Builder builder() {
        return ArmyReactiveStmtOptions.builder();
    }


    interface Builder extends StmtOption.BuilderSpec<Builder> {

        ReactiveStmtOption build();

    }


}
