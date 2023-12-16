package io.army.reactive;

import io.army.session.SessionFactory;
import reactor.core.publisher.Mono;

/**
 * <p>This interface representing a reactive {@link SessionFactory}.
 * <p>The instance of this interface is created by {@link ReactiveFactoryBuilder}
 *
 * @since 0.6.0
 */
public interface ReactiveSessionFactory extends SessionFactory, ReactiveCloseable {

    Mono<ReactiveLocalSession> localSession();

    Mono<ReactiveRmSession> rmSession();


    LocalSessionBuilder localBuilder();


    RmSessionBuilder rmBuilder();


    interface LocalSessionBuilder extends SessionBuilderSpec<LocalSessionBuilder, Mono<ReactiveLocalSession>> {


    }

    interface RmSessionBuilder extends SessionBuilderSpec<RmSessionBuilder, Mono<ReactiveRmSession>> {


    }


}
