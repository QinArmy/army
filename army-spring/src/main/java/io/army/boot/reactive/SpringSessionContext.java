package io.army.boot.reactive;


import io.army.reactive.ReactiveSession;
import io.army.reactive.SessionContext;
import io.army.session.NoCurrentSessionException;
import reactor.core.publisher.Mono;

final class SpringSessionContext implements SessionContext {

    @Override
    public Mono<Boolean> hasCurrentSession() {
        return null;
    }

    @Override
    public Mono<ReactiveSession> currentSession() throws NoCurrentSessionException {
        return null;
    }

}
