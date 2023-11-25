package io.army.reactive;

import io.army.session.FactoryBuilderSpec;
import reactor.core.publisher.Mono;

/**
 * <p>This interface representing the builder of {@link ReactiveSessionFactory}.
 * <p>The instance of This interface is created by {@link #builder()}.
 *
 * @since 1.0
 */
public interface ReactiveFactoryBuilder extends FactoryBuilderSpec<ReactiveFactoryBuilder, Mono<ReactiveSessionFactory>> {


    @Override
    Mono<ReactiveSessionFactory> build();

    static ReactiveFactoryBuilder builder() {
        return ArmyReactiveFactorBuilder.create();
    }


}
