package io.army.reactive;

import io.army.dialect.DialectParser;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.LocalStmtExecutor;
import io.army.session._ArmySessionFactory;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is a implementation of {@link ReactiveLocalSessionFactory}
 */
final class ArmyReactiveLocalSessionFactory extends _ArmySessionFactory implements ReactiveLocalSessionFactory {


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

    static final class LocalSessionBuilder extends ArmySessionBuilder<SessionBuilder, ReactiveSession> implements SessionBuilder {

        final ArmyReactiveLocalSessionFactory sessionFactory;


        LocalStmtExecutor stmtExecutor;
        boolean readOnly;

        private LocalSessionBuilder(ArmyReactiveLocalSessionFactory sessionFactory) {
            super(sessionFactory);
            this.sessionFactory = sessionFactory;
        }


        @Override
        protected ReactiveSession createSession() {
            return null;
        }


    }//SessionBuilder


}
