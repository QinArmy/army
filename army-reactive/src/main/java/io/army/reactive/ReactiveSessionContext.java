package io.army.reactive;

import io.army.session.NoCurrentSessionException;
import io.army.session.SessionContext;
import reactor.core.publisher.Mono;

public interface ReactiveSessionContext extends SessionContext {

    @Override
    ReactiveSessionFactory sessionFactory();

    Mono<Boolean> hasCurrentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws NoCurrentSessionException emit(not throw) when no session in current context.
     */
    Mono<ReactiveSession> currentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws NoCurrentSessionException emit(not throw) when no session in current context.
     */
    <T extends ReactiveSession> Mono<T> currentSession(Class<T> sessionClass);


}
