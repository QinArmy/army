package io.army.reactive;

import io.army.session.NoCurrentSessionException;
import io.army.session.SessionContext;
import reactor.core.publisher.Mono;

public interface ReactiveSessionContext extends SessionContext {

    Mono<Boolean> hasCurrentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws NoCurrentSessionException Typically indicates an issue
     *                                   locating or creating the current session.
     */
    Mono<ReactiveSession> currentSession();


}
