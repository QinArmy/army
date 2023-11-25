package io.army.reactive;

import io.army.session.SessionFactory;
import reactor.core.publisher.Mono;

/**
 * <p>This interface representing a reactive {@link SessionFactory}.
 *
 * @since 1.0
 */
public interface ReactiveSessionFactory extends SessionFactory, ReactiveCloseable {

    LocalSessionBuilder localBuilder();


    RmSessionBuilder rmBuilder();


    interface LocalSessionBuilder extends SessionBuilderSpec<LocalSessionBuilder, Mono<ReactiveLocalSession>> {


    }

    interface RmSessionBuilder extends SessionBuilderSpec<RmSessionBuilder, Mono<ReactiveRmSession>> {


    }


}
