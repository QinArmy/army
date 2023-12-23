package io.army.spring.reactive;


import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionContext;
import io.army.session.NoCurrentSessionException;
import io.army.session.SessionFactory;
import reactor.core.publisher.Mono;

final class SpringSessionContext implements ReactiveSessionContext {

    @Override
    public SessionFactory sessionFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends SessionFactory> T sessionFactory(Class<T> factoryClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Boolean> hasCurrentSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<ReactiveSession> currentSession() throws NoCurrentSessionException {
        throw new UnsupportedOperationException();
    }

}
