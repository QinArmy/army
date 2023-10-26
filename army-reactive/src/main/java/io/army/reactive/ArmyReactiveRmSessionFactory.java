package io.army.reactive;

import io.army.session.SessionFactoryException;
import io.army.session._ArmySessionFactory;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

/**
 * <p>This class is a implementation of {@link ReactiveRmSessionFactory}.
 *
 * @since 1.0
 */
final class ArmyReactiveRmSessionFactory extends ArmyReactiveSessionFactory implements ReactiveRmSessionFactory {


    private ArmyReactiveRmSessionFactory(ArmyReactiveFactorBuilder builder) throws SessionFactoryException {
        super(builder);
    }


    @Override
    public SessionBuilder builder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new RmSessionBuilder(this);
    }

    private static final class RmSessionBuilder extends ReactiveSessionBuilder<SessionBuilder, Mono<ReactiveRmSession>>
            implements SessionBuilder {

        private RmSessionBuilder(ArmyReactiveRmSessionFactory factory) {
            super(factory);
        }

        @Override
        protected Mono<ReactiveRmSession> createSession() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Mono<ReactiveRmSession> handleError(Throwable cause) {
            return Mono.error(_ArmySessionFactory.wrapIfNeed(cause));
        }


    } // RmSessionBuilder

}
