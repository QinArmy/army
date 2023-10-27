package io.army.reactive;

import io.army.session.Option;
import io.army.session.SessionException;
import io.army.session.TransactionInfo;
import io.army.session.TransactionOption;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * <p>This interface representing reactive local session that support database local transaction.
 *
 * @see ReactiveLocalSessionFactory
 * @since 1.0
 */
public interface ReactiveLocalSession extends ReactiveSession {

    @Override
    ReactiveLocalSessionFactory sessionFactory();

    Mono<TransactionInfo> startTransaction();

    Mono<TransactionInfo> startTransaction(TransactionOption option);

    @Override
    ReactiveLocalSession markRollbackOnly() throws SessionException;


    Mono<ReactiveLocalSession> commit();

    Mono<ReactiveLocalSession> commit(Function<Option<?>, ?> optionFunc);


    Mono<ReactiveLocalSession> rollback();

    Mono<ReactiveLocalSession> rollback(Function<Option<?>, ?> optionFunc);


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
