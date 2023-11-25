package io.army.reactive;

import io.army.session.FactoryBuilderSpec;
import reactor.core.publisher.Mono;


/**
 * <p>This interface representing the builder of {@link ReactiveRmSessionFactory}.
 * <p>The instance of This interface is created by {@link #builder()}.
 *
 * @since 1.0
 */
public interface ReactiveRmFactoryBuilder
        extends FactoryBuilderSpec<ReactiveRmFactoryBuilder, Mono<ReactiveRmSessionFactory>> {


    @Override
    Mono<ReactiveRmSessionFactory> build();

    static ReactiveRmFactoryBuilder builder() {
        return ArmyRmSessionFactoryBuilder.create();
    }


}
