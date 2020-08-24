package io.army.reactive;

import io.army.GenericRmSessionFactory;
import reactor.core.publisher.Mono;

public interface ReactiveSessionFactory extends ReactiveApiSessionFactory, GenericRmSessionFactory {

    ReactiveSessionBuilder builder();


    interface ReactiveSessionBuilder {

        ReactiveSessionBuilder currentSession(boolean current);

        ReactiveSessionBuilder readOnly(boolean readOnly);

        Mono<ReactiveSession> build();
    }

}
