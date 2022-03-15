package io.army.reactive;

import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.tx.CannotCreateTransactionException;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.reactive.ReactiveTransaction;
import reactor.core.publisher.Mono;

/**
 * @see SessionFactory
 */
public interface Session extends SingleDatabaseReactiveSession, GenericReactiveApiSession {

    @Override
    SessionFactory sessionFactory();

    // @Override
    ReactiveTransaction sessionTransaction() throws NoSessionTransactionException;

    Mono<Void> close() throws SessionException;

    /**
     * @throws CannotCreateTransactionException throw when session already have {@link ReactiveTransaction}
     */
    SessionTransactionBuilder builder() throws CannotCreateTransactionException;

    interface SessionTransactionBuilder {

        SessionTransactionBuilder readOnly(boolean readOnly);

        SessionTransactionBuilder isolation(Isolation isolation);

        SessionTransactionBuilder timeout(int seconds);

        SessionTransactionBuilder name(@Nullable String txName);

        /**
         * @throws CannotCreateTransactionException throw when
         *                                          <ul>
         *                                              <li>not specified {@link Isolation}</li>
         *                                              <li>{@link Session#readonly()} equals {@code true} but,specified transaction readOnly</li>
         *                                              <li>session already have {@link ReactiveTransaction}</li>
         *                                          </ul>
         */
        ReactiveTransaction build() throws CannotCreateTransactionException;
    }
}
