package io.army.reactive;

import reactor.core.publisher.Mono;

/**
 * <p>This interface representing reactive RM(Resource Manager) session factory in XA transaction.
 *
 * @since 1.0
 */
public interface ReactiveRmSessionFactory extends ReactiveSessionFactory {


    SessionBuilder builder();


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, Mono<ReactiveRmSession>> {


    }


}
