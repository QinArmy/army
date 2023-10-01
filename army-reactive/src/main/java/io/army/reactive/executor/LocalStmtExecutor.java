package io.army.reactive.executor;

import io.army.session.Option;
import io.army.session.TransactionOption;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface LocalStmtExecutor extends StmtExecutor {

    Mono<Void> startTransaction(TransactionOption option);

    Mono<Void> commit(Function<Option<?>, ?> optionFunc);


    Mono<Void> rollback(Function<Option<?>, ?> optionFunc);


}
