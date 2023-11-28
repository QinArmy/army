package io.army.spring.reactive;


import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionContext;
import io.army.session.NoCurrentSessionException;
import reactor.core.publisher.Mono;

final class SpringSessionContext implements ReactiveSessionContext {

    @Override
    public Mono<Boolean> hasCurrentSession() {
        return null;
    }

    @Override
    public Mono<ReactiveSession> currentSession() throws NoCurrentSessionException {
        return null;
    }

}
