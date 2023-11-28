package io.army.reactive;

import io.army.session.*;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>This interface representing reactive local session that support database local transaction.
 *
 * @see ReactiveSessionFactory
 * @since 1.0
 */
public interface ReactiveLocalSession extends ReactiveSession, LocalSession {


    /**
     * <p>Start pseudo transaction that don't start real local transaction.
     * <p>Pseudo transaction is designed for some framework in readonly transaction,for example {@code org.springframework.transaction.TransactionManager}.
     *
     * @throws IllegalArgumentException                  emit when
     *                                                   <ul>
     *                                                       <li>{@link TransactionOption#isolation()} isn't {@link Isolation#PSEUDO}</li>
     *                                                       <li>{@link TransactionOption#isReadOnly()} is false</li>
     *                                                   </ul>
     * @throws java.util.ConcurrentModificationException emit when concurrent control transaction
     * @throws SessionException                          emit when
     *                                                   <ul>
     *                                                       <li>session have closed</li>
     *                                                       <li>{@link #isReadonlySession()} is false</li>
     *                                                       <li>mode is {@link HandleMode#ERROR_IF_EXISTS} and {@link #hasTransactionInfo()} is true</li>
     *                                                       <li>mode is {@link HandleMode#COMMIT_IF_EXISTS} and commit failure</li>
     *                                                       <li>mode is {@link HandleMode#ROLLBACK_IF_EXISTS} and rollback failure</li>
     *                                                   </ul>
     */
    Mono<TransactionInfo> pseudoTransaction(TransactionOption option, HandleMode mode);


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
