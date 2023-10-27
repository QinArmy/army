package io.army.reactive;

import io.army.session.ResultStates;
import io.army.session.StmtOption;

import java.util.function.Consumer;

public interface ReactiveStmtOption extends StmtOption {

    Consumer<ResultStates> stateConsumer();


}
