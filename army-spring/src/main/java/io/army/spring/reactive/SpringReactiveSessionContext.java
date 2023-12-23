package io.army.spring.reactive;


import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionContext;
import io.army.reactive.ReactiveSessionFactory;
import io.army.session.NoCurrentSessionException;
import io.army.session.SessionFactory;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

final class SpringReactiveSessionContext implements ReactiveSessionContext {

    static SpringReactiveSessionContext create(ReactiveSessionFactory factory) {
        return new SpringReactiveSessionContext(factory);
    }

    private final ReactiveSessionFactory factory;

    private SpringReactiveSessionContext(ReactiveSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ReactiveSessionFactory sessionFactory() {
        return this.factory;
    }

    @Override
    public <T extends SessionFactory> T sessionFactory(Class<T> factoryClass) {
        return factoryClass.cast(this.factory);
    }

    @Override
    public Mono<Boolean> hasCurrentSession() {
        return TransactionSynchronizationManager.forCurrentTransaction()
                .map(manager -> manager.hasResource(this.factory));
    }


    @Override
    public Mono<ReactiveSession> currentSession() {
        return currentSession(ReactiveSession.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ReactiveSession> Mono<T> currentSession(final Class<T> sessionClass) {
        return TransactionSynchronizationManager.forCurrentTransaction()
                .flatMap(manager -> {
                    final Object value;
                    value = manager.getResource(this.factory);
                    if (sessionClass.isInstance(value)) {
                        return Mono.just((T) value);
                    }
                    return Mono.error(new NoCurrentSessionException("no current session"));
                });
    }


}
