package io.army.reactive;

import io.army.GenericRmSessionFactory;
import io.army.SessionException;
import reactor.core.publisher.Mono;

public interface ReactiveSessionFactory extends GenericReactiveApiSessionFactory, GenericRmSessionFactory {

    @Override
    ProxyReactiveSession proxySession();

    ReactiveSessionBuilder builder();


    interface ReactiveSessionBuilder {

        ReactiveSessionBuilder currentSession(boolean current);

        ReactiveSessionBuilder readOnly(boolean readOnly);

        Mono<ReactiveSession> build() throws SessionException;
    }

}
