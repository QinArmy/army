package io.army.boot.reactive;

import io.army.NoCurrentSessionException;
import io.army.reactive.GenericReactiveTmSession;
import reactor.core.publisher.Mono;

interface CurrentSessionContext {

    Mono<Boolean> hasCurrentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws NoCurrentSessionException Typically indicates an issue
     *                                   locating or creating the current session.
     */
    Mono<GenericReactiveTmSession> currentSession() throws NoCurrentSessionException;



}
