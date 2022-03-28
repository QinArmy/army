package io.army.reactive;

import io.army.lang.Nullable;
import io.army.session.CannotCreateTransactionException;
import io.army.session.SessionException;
import io.army.tx.Isolation;
import reactor.core.publisher.Mono;

/**
 * @see SessionFactory
 */
public interface Session extends ReactiveSession {


    Mono<Void> close() throws SessionException;

    /**
     * @throws CannotCreateTransactionException throw when session already have {@link Transaction}
     */
    TransactionBuilder builder() throws SessionException;

    interface TransactionBuilder {

        TransactionBuilder readOnly(boolean readOnly);

        TransactionBuilder isolation(Isolation isolation);

        TransactionBuilder timeout(int seconds);

        TransactionBuilder name(@Nullable String txName);

        /**
         * @throws CannotCreateTransactionException throw when
         *                                          <ul>
         *                                              <li>not specified {@link Isolation}</li>
         *                                              <li>{@link Session#isReadonlySession()} equals {@code true} but,specified transaction readOnly</li>
         *                                              <li>session already have {@link Transaction}</li>
         *                                          </ul>
         */
        Transaction build() throws SessionException;
    }
}
