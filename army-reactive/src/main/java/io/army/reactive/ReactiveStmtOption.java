package io.army.reactive;

import io.army.session.ResultStates;
import io.army.session.StatementOption;

import java.util.function.Consumer;

public interface ReactiveStmtOption extends StatementOption {

    Consumer<ResultStates> stateConsumer();


}
