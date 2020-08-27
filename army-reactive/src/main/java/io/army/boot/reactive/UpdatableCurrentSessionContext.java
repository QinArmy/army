package io.army.boot.reactive;

import io.army.reactive.GenericReactiveTmSession;
import reactor.core.publisher.Mono;

interface UpdatableCurrentSessionContext extends CurrentSessionContext {

    Mono<Void> currentSession(GenericReactiveTmSession session) throws IllegalStateException;

    Mono<Void> removeCurrentSession(GenericReactiveTmSession session) throws IllegalStateException;

}
