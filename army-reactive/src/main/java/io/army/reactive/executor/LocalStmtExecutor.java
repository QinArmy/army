package io.army.reactive.executor;

import io.army.session.Option;
import io.army.session.TransactionOption;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface LocalStmtExecutor extends StmtExecutor {

    Mono<LocalStmtExecutor> startTransaction(TransactionOption option);

    Mono<LocalStmtExecutor> commit(Function<Option<?>, ?> optionFunc);


    Mono<LocalStmtExecutor> rollback(Function<Option<?>, ?> optionFunc);


}
