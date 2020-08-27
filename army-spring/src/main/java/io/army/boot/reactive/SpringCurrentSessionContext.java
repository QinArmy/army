package io.army.boot.reactive;

import io.army.NoCurrentSessionException;
import io.army.reactive.GenericReactiveApiSession;
import io.army.reactive.ReactiveApiSessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

final class SpringCurrentSessionContext implements CurrentSessionContext {

    static SpringCurrentSessionContext build(ReactiveApiSessionFactory sessionFactory) {
        return new SpringCurrentSessionContext(sessionFactory);
    }

    private final ReactiveApiSessionFactory sessionFactory;

    private SpringCurrentSessionContext(ReactiveApiSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Mono<Boolean> hasCurrentSession() {
        return TransactionSynchronizationManager.forCurrentTransaction()
                .map(manager -> manager.hasResource(this.sessionFactory));

    }

    @Override
    public Mono<GenericReactiveApiSession> currentSession() throws NoCurrentSessionException {
        return TransactionSynchronizationManager.forCurrentTransaction()
                .flatMap(manager -> Mono.justOrEmpty(manager.getResource(this.sessionFactory)))
                .switchIfEmpty(noCurrentSessionError())
                .cast(GenericReactiveApiSession.class)
                ;
    }

    private Mono<GenericReactiveApiSession> noCurrentSessionError() {
        return Mono.defer(() -> Mono.error(new NoCurrentSessionException(
                "not found current session ,please check %s on transaction method."
                , Transactional.class.getName())));
    }
}
