package io.army.reactive;

import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is a implementation of {@link ReactiveLocalSessionFactory}
 */
final class ArmyReactiveLocalSessionFactory extends ArmyReactiveSessionFactory implements ReactiveLocalSessionFactory {


    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    ArmyReactiveLocalSessionFactory(LocalSessionFactoryBuilder builder) {
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


        private LocalSessionBuilder(ArmyReactiveLocalSessionFactory sessionFactory) {
            super(sessionFactory);
        }

        @Override
        protected Mono<ReactiveLocalSession> createSession(String name) {
            return ((ArmyReactiveLocalSessionFactory) this.factory).stmtExecutorFactory
                    .localExecutor(name)
                    .map(this::createLocalSession);
        }

        @Override
        protected Mono<ReactiveLocalSession> handleError(Throwable cause) {
            return Mono.error(_Exceptions.wrapIfNeed(cause));
        }

        private ReactiveLocalSession createLocalSession(final ReactiveLocalStmtExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return new ArmyReactiveLocalSession(this);
        }

    }//SessionBuilder


}
