package io.army.reactive;

import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.session.SessionException;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

/**
 * This class is a implementation of {@link ReactiveLocalSessionFactory}
 *
 * @see ArmyReactiveLocalSession
 * @since 1.0
 */
final class ArmyReactiveLocalSessionFactory extends ArmyReactiveSessionFactory implements ReactiveLocalSessionFactory {


    static ArmyReactiveLocalSessionFactory create(ArmyLocalSessionFactoryBuilder builder) {
        return new ArmyReactiveLocalSessionFactory(builder);
    }


    /**
     * private constructor
     */
    private ArmyReactiveLocalSessionFactory(ArmyLocalSessionFactoryBuilder builder) {
        super(builder);
    }


    /*################################## blow InnerReactiveApiSessionFactory method ##################################*/

    @Override
    public ReactiveLocalSessionFactory.SessionBuilder builder() {
        return new LocalSessionBuilder(this);
    }

    /*################################## blow private static inner class ##################################*/

    static final class LocalSessionBuilder extends ReactiveSessionBuilder<SessionBuilder, Mono<ReactiveLocalSession>>
            implements SessionBuilder {

        /**
         * private constructor
         */
        private LocalSessionBuilder(ArmyReactiveLocalSessionFactory sessionFactory) {
            super(sessionFactory);
        }

        @Override
        protected Mono<ReactiveLocalSession> createSession(String sessionName, boolean readonly) {
            return ((ArmyReactiveLocalSessionFactory) this.factory).stmtExecutorFactory
                    .localExecutor(sessionName, readonly)
                    .map(this::createLocalSession);
        }


        @Override
        protected Mono<ReactiveLocalSession> handleError(SessionException cause) {
            return Mono.error(_Exceptions.wrapIfNeed(cause));
        }

        private ReactiveLocalSession createLocalSession(final ReactiveLocalStmtExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return ArmyReactiveLocalSession.create(this);
        }

    } // LocalSessionBuilder


}
