package io.army.reactive;

import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.tx.Isolation;
import io.army.tx.reactive.ReactiveTransaction;
import reactor.core.publisher.Mono;

public interface ReactiveSession extends GenericReactiveApiSession {


    ReactiveTransaction sessionTransaction();

    SessionTransactionBuilder builder();

    Mono<Void> close() throws SessionException;

    Mono<Void> flush() throws SessionException;

    interface SessionTransactionBuilder {

        SessionTransactionBuilder readOnly(boolean readOnly);

        SessionTransactionBuilder isolation(Isolation isolation);

        SessionTransactionBuilder timeout(int seconds);

        SessionTransactionBuilder name(@Nullable String txName);

        Mono<ReactiveTransaction> build();
    }
}
