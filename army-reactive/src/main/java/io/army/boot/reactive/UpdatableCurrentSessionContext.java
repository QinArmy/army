package io.army.boot.reactive;

import io.army.reactive.GenericReactiveApiSession;
import reactor.core.publisher.Mono;

interface UpdatableCurrentSessionContext extends CurrentSessionContext {

    Mono<Void> currentSession(GenericReactiveApiSession session) throws IllegalStateException;

    Mono<Void> removeCurrentSession(GenericReactiveApiSession session) throws IllegalStateException;

}
