package io.army.reactive;

import io.army.GenericSessionFactory;
import reactor.core.publisher.Mono;

public interface GenericReactiveSessionFactory extends GenericSessionFactory {


    Mono<Void> close();

}
