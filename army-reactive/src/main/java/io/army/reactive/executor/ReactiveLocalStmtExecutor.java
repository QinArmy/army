package io.army.reactive.executor;

import io.army.session.Option;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

public interface ReactiveLocalStmtExecutor extends ReactiveStmtExecutor {

    Mono<TransactionInfo> startTransaction(TransactionOption option);

    Mono<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc);

    Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc);


}
