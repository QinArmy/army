package io.army.reactive;

import reactor.core.publisher.Mono;

public interface ReactiveSessionFactory extends GenericReactiveApiSessionFactory {

    ReactiveSessionBuilder builder();


    interface ReactiveSessionBuilder {

        ReactiveSessionBuilder currentSession();

        Mono<ReactiveSession> build();
    }

}
