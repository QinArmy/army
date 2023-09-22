package io.army.reactive;

import io.army.session.ResultStates;
import io.army.session.StatementOptionSpec;

import java.util.function.Consumer;

public interface ReactiveOption extends StatementOptionSpec {

    Consumer<ResultStates> stateConsumer();

}
