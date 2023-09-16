package io.army.reactive;

import io.army.dialect.DialectParser;
import io.army.meta.ServerMeta;
import io.army.session._ArmySessionFactory;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is a implementation of {@link ReactiveSessionFactory}
 */
final class ArmyReactiveLocalSessionFactory extends _ArmySessionFactory implements ReactiveSessionFactory {


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
    public ReactiveSessionFactory.SessionBuilder builder() {
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

        private final ArmyReactiveLocalSessionFactory sessionFactory;


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
