package io.army;

import reactor.core.publisher.Mono;

public interface ReactiveSessionFactory extends GenericSessionFactory {

    ReactiveSessionBuilder builder();

    ProxyReactiveSession proxySession();

    interface ReactiveSessionBuilder {

        ReactiveSessionBuilder currentSession();

        Mono<ReactiveSession> build();
    }

}
