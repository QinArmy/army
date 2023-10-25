package io.army.reactive;

import reactor.core.publisher.Mono;

public interface ReactiveLocalSessionFactory extends ReactiveSessionFactory {


    SessionBuilder builder();


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, Mono<ReactiveLocalSession>> {


    }

}
