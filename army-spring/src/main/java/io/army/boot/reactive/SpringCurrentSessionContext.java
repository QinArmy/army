package io.army.boot.reactive;

import io.army.reactive.GenericReactiveApiSession;
import io.army.reactive.GenericReactiveApiSessionFactory;
import io.army.session.NoCurrentSessionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

final class SpringCurrentSessionContext implements CurrentSessionContext {

    static SpringCurrentSessionContext build(GenericReactiveApiSessionFactory sessionFactory) {
        return new SpringCurrentSessionContext(sessionFactory);
    }

    private final GenericReactiveApiSessionFactory sessionFactory;

    private SpringCurrentSessionContext(GenericReactiveApiSessionFactory sessionFactory) {
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
