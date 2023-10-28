package io.army.reactive;

import io.army.session.*;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>This interface representing reactive local session that support database local transaction.
 *
 * @see ReactiveLocalSessionFactory
 * @since 1.0
 */
public interface ReactiveLocalSession extends ReactiveSession, LocalSession {

    @Override
    ReactiveLocalSessionFactory sessionFactory();

    Mono<TransactionInfo> startTransaction();

    Mono<TransactionInfo> startTransaction(TransactionOption option);


    Mono<TransactionInfo> startTransaction(TransactionOption option, HandleMode mode);

    Mono<ReactiveLocalSession> commit();

    Mono<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc);

    Mono<ReactiveLocalSession> rollback();

    Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc);


    @Override
    Mono<ReactiveLocalSession> setTransactionCharacteristics(TransactionOption option);

    @Override
    Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint);


    @Override
    Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);

    @Override
    Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint);

    @Override
    Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc);


}
