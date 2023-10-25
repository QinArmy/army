package io.army.reactive;

import io.army.dialect.DialectParser;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.LocalStmtExecutor;
import io.army.reactive.executor.StmtExecutorFactory;
import io.army.util._Exceptions;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is a implementation of {@link ReactiveLocalSessionFactory}
 */
final class ArmyReactiveLocalSessionFactory extends ArmyReactiveSessionFactory implements ReactiveLocalSessionFactory {


    private StmtExecutorFactory stmtExecutorFactory;

    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    ArmyReactiveLocalSessionFactory(LocalSessionFactoryBuilder builder) {
        super(builder);
    }


    @Override
    public ServerMeta serverMeta() {
        return null;
    }

    @Override
    public ZoneId zoneId() {
        return null;
    }

    @Override
    public boolean isSupportSavePoints() {
        return false;
    }

    @Override
    public boolean isReactive() {
        return true;
    }


    /*################################## blow InnerReactiveApiSessionFactory method ##################################*/


    @Override
    public boolean isClosed() {
        return this.factoryClosed.get();
    }

    @Override
    public ReactiveLocalSessionFactory.SessionBuilder builder() {
        return new LocalSessionBuilder(this);
    }

    @Override
    public Mono<Void> close() {
        return Mono.empty();
    }

    @Override
    protected DialectParser dialectParser() {
        throw new UnsupportedOperationException();
    }

    /*################################## blow private static inner class ##################################*/

    static final class LocalSessionBuilder extends ReactiveSessionBuilder<SessionBuilder, Mono<ReactiveLocalSession>>
            implements SessionBuilder {


        private LocalSessionBuilder(ArmyReactiveLocalSessionFactory sessionFactory) {
            super(sessionFactory);
        }

        @Override
        protected Mono<ReactiveLocalSession> createSession() {
            return ((ArmyReactiveLocalSessionFactory) this.armyFactory).stmtExecutorFactory
                    .localStmtExecutor()
                    .map(this::createLocalSession);
        }

        @Override
        protected Mono<ReactiveLocalSession> handleError(Throwable cause) {
            return Mono.error(_Exceptions.wrapIfNeed(cause));
        }

        private ReactiveLocalSession createLocalSession(final LocalStmtExecutor stmtExecutor) {
            this.stmtExecutor = stmtExecutor;
            return new ArmyReactiveLocalSession(this);
        }

    }//SessionBuilder


}
