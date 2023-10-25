package io.army.reactive;

import io.army.session.CloseableSpec;
import reactor.core.publisher.Mono;


public interface ReactiveCloseable extends CloseableSpec {

    <T> Mono<T> close();

}
