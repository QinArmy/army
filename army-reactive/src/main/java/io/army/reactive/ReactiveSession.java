package io.army.reactive;

import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.tx.Isolation;
import io.army.tx.reactive.ReactiveTransaction;
import reactor.core.publisher.Mono;

public interface ReactiveSession extends SingleDatabaseReactiveSession, GenericReactiveRmSession {

    @Override
    ReactiveTransaction sessionTransaction();

    Mono<Void> close() throws SessionException;

    SessionTransactionBuilder builder();

    interface SessionTransactionBuilder {

        SessionTransactionBuilder readOnly(boolean readOnly);

        SessionTransactionBuilder isolation(Isolation isolation);

        SessionTransactionBuilder timeout(int seconds);

        SessionTransactionBuilder name(@Nullable String txName);

        Mono<ReactiveTransaction> build();
    }
}
