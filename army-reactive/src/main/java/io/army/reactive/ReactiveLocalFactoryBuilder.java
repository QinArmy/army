package io.army.reactive;

import io.army.session.FactoryBuilderSpec;
import reactor.core.publisher.Mono;

/**
 * <p>This interface representing the builder of {@link ReactiveLocalSessionFactory}.
 * <p>The instance of This interface is created by {@link #builder()}.
 *
 * @since 1.0
 */
public interface ReactiveLocalFactoryBuilder extends FactoryBuilderSpec<ReactiveLocalFactoryBuilder, Mono<ReactiveLocalSessionFactory>> {


    @Override
    Mono<ReactiveLocalSessionFactory> build();

    static ReactiveLocalFactoryBuilder builder() {
        return ArmyLocalSessionFactoryBuilder.create();
    }


}
