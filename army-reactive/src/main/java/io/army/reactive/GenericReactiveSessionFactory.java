package io.army.reactive;

import io.army.session.GenericSessionFactory;
import reactor.core.publisher.Mono;

public interface GenericReactiveSessionFactory extends GenericSessionFactory {


    Mono<Void> close();

}
