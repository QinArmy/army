package io.army.context.spi;

import io.army.DataAccessException;
import io.army.NoCurrentSessionException;
import io.army.reactive.GenericReactiveApiSession;
import reactor.core.publisher.Mono;

public interface ReactiveCurrentSessionContext {

    boolean hasCurrentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws DataAccessException Typically indicates an issue
     *                             locating or creating the current session.
     */
    Mono<GenericReactiveApiSession> currentSession() throws NoCurrentSessionException;

    Mono<Void> currentSession(boolean current, GenericReactiveApiSession session) throws IllegalStateException;

    Mono<Void> removeCurrentSession(boolean current, GenericReactiveApiSession session) throws IllegalStateException;

}
