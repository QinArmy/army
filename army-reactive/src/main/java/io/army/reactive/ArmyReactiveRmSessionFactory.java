package io.army.reactive;

import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.session.SessionException;
import io.army.session.SessionFactoryException;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

/**
 * <p>This class is a implementation of {@link ReactiveRmSessionFactory}.
 *
 * @since 1.0
 */
final class ArmyReactiveRmSessionFactory extends ArmyReactiveSessionFactory implements ReactiveRmSessionFactory {

    /**
     * @see ArmyRmSessionFactoryBuilder#createSessionFactory()
     */
    static ArmyReactiveRmSessionFactory create(ArmyRmSessionFactoryBuilder builder) {
        return new ArmyReactiveRmSessionFactory(builder);
    }


    /**
     * private constructor
     */
    private ArmyReactiveRmSessionFactory(ArmyRmSessionFactoryBuilder builder) throws SessionFactoryException {
        super(builder);
    }


    @Override
    public SessionBuilder builder() {
        if (isClosed()) {
            throw _Exceptions.sessionFactoryClosed(this);
        }
        return new RmSessionBuilder(this);
    }

    static final class RmSessionBuilder extends ReactiveSessionBuilder<SessionBuilder, Mono<ReactiveRmSession>>
            implements SessionBuilder {

        /**
         * private constructor
         */
        private RmSessionBuilder(ArmyReactiveRmSessionFactory factory) {
            super(factory);
        }


        @Override
        protected Mono<ReactiveRmSession> createSession(String sessionName, boolean readonly) {
            return ((ArmyReactiveLocalSessionFactory) this.factory).stmtExecutorFactory
                    .rmExecutor(sessionName, readonly)
                    .map(this::createRmSession);
        }

        @Override
        protected Mono<ReactiveRmSession> handleError(SessionException cause) {
            return Mono.error(_Exceptions.wrapIfNeed(cause));
        }

        private ReactiveRmSession createRmSession(final ReactiveRmStmtExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return ArmyReactiveRmSession.create(this);
        }


    } // RmSessionBuilder

}
