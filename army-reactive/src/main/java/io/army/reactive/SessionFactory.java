package io.army.reactive;

import io.army.SessionException;
import io.army.session.DialectSessionFactory;
import reactor.core.publisher.Mono;

public interface SessionFactory extends GenericReactiveApiSessionFactory, DialectSessionFactory {

    @Override
    ProxyReactiveSession proxySession();

    ReactiveSessionBuilder builder();


    interface ReactiveSessionBuilder {

        ReactiveSessionBuilder currentSession(boolean current);

        ReactiveSessionBuilder readOnly(boolean readOnly);

        Mono<Session> build() throws SessionException;
    }

}